package org.jetbrains.reflekt.plugin.analysis.ir

import org.jetbrains.reflekt.plugin.analysis.ir.FunctionTypeWrapper.Companion.wrap

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.jvm.codegen.isExtensionFunctionType
import org.jetbrains.kotlin.ir.backend.js.utils.asString
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.descriptors.IrBuiltIns
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.types.Variance

/**
 * Represents function type with receiver, arguments, and return type, i.e:
 *
 *    receiver            return type
 *       v                     v
 *      Int.(Any, Boolean) -> Unit
 *                ^
 *          list of arguments
 *
 * If a function has no receiver, i.e. (Any, Boolean) -> Unit, it sets to null.
 * Also, it might be suspend (see [specification](https://kotlinlang.org/spec/type-system.html#suspending-function-types))
 *
 * @param isSuspend denotes whether this function is a suspend one
 * @param receiver extension receiver of function, might be null if it's not an extension
 * @param arguments list of function arguments, might be empty
 * @param returnType
 *
 * ToDo: we're not supporting vararg functions and member functions (i.e. with dispatch receiver)
 */
data class FunctionTypeWrapper(
    val isSuspend: Boolean,
    val receiver: IrType?,
    var arguments: List<IrTypeArgument>,
    var returnType: IrType,
) {
    /**
     * Checks whether this function type is a subtype of other function type by separately checking their receivers, return types, and arguments.
     * By subtyping, we take the following teh classic definition of substitutability:
     *      Type S of a function ::foo is a subtype of another type T (S <: T), if at any place type T is required, we can pass ::foo.
     *      For example, keeping in mind String <: CharSequence <: Any and Int <: Number <: Any, given a function
     *          fun CharSequence.foo(a: Any, b: Int): Int { }  // CharSequence.(Any, Number) -> Int
     *
     *      and a variable with type String.(Int, Int) -> Number
     *          val t: String.(Int, Int) -> Number
     *
     *     we can call initialize t with a type of foo:
     *          val t: String.(Int, Int) -> Number = CharSequence::foo
     *
     **     Note, that receiver type E is contravariant (out E), arguments types A are contravariant (in A), while return type R is covariant (out R),
     *      therefore function type F is a parametrized type with the following notation ([] denotes optional):
     *          F<[in E], [in A1, in A2, .., in AN], out R>
     *      See how subtyping between parametrized types works [here](https://kotlinlang.org/spec/type-system.html#mixed-site-variance)
     *
     * @param superType
     * @param builtIns
     * @return whether this is a subtype of [superType]
     */
    fun isSubtypeOf(superType: FunctionTypeWrapper, builtIns: IrBuiltIns): Boolean =
        isSuspendSubtype(superType) &&
            isReceiverSubtype(superType, builtIns) &&
            isReturnSubtype(superType, builtIns) &&
            isArgumentsSubtype(superType, builtIns)

    /**
     * To be a subtype, it needs to have the same 'suspending' state, i.e. both should be suspend or both should be non-suspend.
     * See [specification](https://kotlinlang.org/spec/type-system.html#suspending-function-types)
     *
     * @param superType a type to compare super typing with
     */
    private fun isSuspendSubtype(superType: FunctionTypeWrapper): Boolean = this.isSuspend == superType.isSuspend

    /**
     * Checks whether return types of both function types satisfy "functions' subtype relation".
     * Given types () -> S and () -> T, or F<out S> and F<out T>, to be subtypes, i.e F<out S> <: F<out T>, we need S <: T
     *
     * @param superType a type to compare super typing with
     */
    private fun isReturnSubtype(superType: FunctionTypeWrapper, builtIns: IrBuiltIns): Boolean = returnType.isSubtypeOf(superType.returnType, builtIns)

    /**
     * Checks whether arguments of both functions have the same size and satisfy subtyping condition.
     * TODO: what will happen if there are default values and therefore the number of arguments might not be constant?
     *
     * Given types (S) -> Unit and (T) -> Unit, or F<in S, out Unit> and F<in T, out Unit>, to be subtypes,
     * i.e F<in S, out Unit> <: F<in T, out Unit>, we need T <: S.
     *
     * @param superType a type to compare super typing with
     * @param builtIns
     */
    private fun isArgumentsSubtype(superType: FunctionTypeWrapper, builtIns: IrBuiltIns): Boolean = arguments.size == superType.arguments.size &&
        arguments.zip(superType.arguments).all { (thisArgument, otherArgument) ->
            otherArgument.isSubtypeOf(thisArgument, builtIns)
        }

    /**
     * Checks whether a receiver of this is a subtype of receiver of [superType].
     * If both receivers are NOT null, checks subtyping; if both receivers are null, returns true; otherwise return false.
     *
     * Given types S.() -> Unit and T.() -> Unit, or F<in S, out Unit> and F<in T, out Unit>, to be subtypes,
     * i.e F<in S, out Unit> <: F<in T, out Unit>, we need T <: S.
     *
     * @param superType a type to compare super typing with
     * @param builtIns
     */
    private fun isReceiverSubtype(superType: FunctionTypeWrapper, builtIns: IrBuiltIns): Boolean = if (this.receiver != null && superType.receiver != null) {
        superType.receiver.isSubtypeOf(this.receiver, builtIns)
    } else {
        this.receiver == null && superType.receiver == null
    }

    companion object {
        /**
         * Wraps an [IrFunction] and fills every required property.
         */
        fun IrFunction.wrap(): FunctionTypeWrapper = FunctionTypeWrapper(
            isSuspend,
            extensionReceiverParameter?.type,
            valueParameters.map { it.type.makeTypeProjection() },
            returnType,
        )

        /**
         * Wraps an [IrSimpleType], checking that it is a function (otherwise, returning a null) and filling every required property.
         */
        fun IrSimpleType.wrap(): FunctionTypeWrapper? {
            if (!isFunction()) {
                return null
            }
            val arguments = this.arguments.toMutableList()
            val returnType = arguments.removeLastOrNull()?.typeOrNull ?: return null
            val receiver = if (isExtensionFunctionType) {
                arguments.removeAt(0).typeOrNull
            } else {
                null
            }
            return FunctionTypeWrapper(isSuspendFunction(), receiver, arguments, returnType)
        }
    }
}

fun IrFunction.isSubtypeOf(other: IrFunction, builtIns: IrBuiltIns) = this.wrap().isSubtypeOf(other.wrap(), builtIns)

fun IrFunction.isSubtypeOf(other: IrType, pluginContext: IrPluginContext): Boolean = (other as? IrSimpleType)?.wrap()?.let {
    this.wrap().isSubtypeOf(it, pluginContext.irBuiltIns)
} ?: false

fun IrTypeArgument.isSubtypeOf(superType: IrTypeArgument, irBuiltIns: IrBuiltIns): Boolean {
    this.typeOrNull ?: error("Can not get type from IrTypeArgument: ${this.asString()}")
    superType.typeOrNull ?: error("Can not get type from IrTypeArgument: ${superType.asString()}")
    return this.typeOrNull!!.isSubtypeOf(superType.typeOrNull!!, irBuiltIns)
}

// TODO: Move to other util?
private fun IrTypeArgument.asString(): String = when (this) {
    is IrStarProjection -> "*"
    is IrTypeProjection -> variance.label + (if (variance != Variance.INVARIANT) " " else "") + type.asString()
    else -> error("Unexpected kind of IrTypeArgument: ${javaClass.simpleName}")
}
