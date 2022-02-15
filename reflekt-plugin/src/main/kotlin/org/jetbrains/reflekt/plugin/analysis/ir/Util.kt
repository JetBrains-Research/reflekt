@file:OptIn(ObsoleteDescriptorBasedAPI::class)

package org.jetbrains.reflekt.plugin.analysis.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.serialization.signature.IdSignatureDescriptor
import org.jetbrains.kotlin.backend.jvm.codegen.psiElement
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.backend.js.utils.asString
import org.jetbrains.kotlin.ir.backend.jvm.serialization.JvmManglerDesc
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.descriptors.IrBuiltIns
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.types.impl.IrSimpleTypeImpl
import org.jetbrains.kotlin.ir.types.impl.makeTypeProjection
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.library.metadata.KlibMetadataProtoBuf.fqName
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi2ir.generators.TypeTranslatorImpl
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.scopes.receivers.TransientReceiver
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.reflekt.plugin.analysis.models.ir.IrFunctionInfo
import org.jetbrains.reflekt.plugin.analysis.psi.function.toParameterizedType

fun IrCall.getFqNamesOfTypeArguments(): List<String> {
    val result = ArrayList<String>()
    for (i in 0 until typeArgumentsCount) {
        val type = getTypeArgument(i)
        require(type is IrSimpleType)
        result.add(type.classFqName.toString())
    }
    return result
}

fun IrCall.getFqNamesOfClassReferenceValueArguments(): List<String> =
    (getValueArgument(0) as? IrVararg)?.elements?.map {
        (it as IrClassReference).classType.classFqName.toString()
    } ?: emptyList()

@OptIn(ObsoleteDescriptorBasedAPI::class)
fun IrType.toParameterizedType() = toKotlinType()

fun IrFunction.toParameterizedType(binding: BindingContext): KotlinType? = (psiElement as? KtNamedFunction)?.toParameterizedType(binding)

fun IrClass.isSubTypeOf(type: IrType, pluginContext: IrPluginContext) = this.defaultType.isSubtypeOf(type, this.createIrBuiltIns(pluginContext))

fun IrDeclaration.createIrBuiltIns(pluginContext: IrPluginContext): IrBuiltIns {
    val symbolTable = SymbolTable(IdSignatureDescriptor(JvmManglerDesc()), pluginContext.irFactory)
    val typeTranslator = TypeTranslatorImpl(symbolTable, pluginContext.languageVersionSettings, this.module)
    return IrBuiltIns(this.module.builtIns, typeTranslator, symbolTable)
}

// TODO: Move into other util?
private fun IrTypeArgument.asString(): String = when (this) {
    is IrStarProjection -> "*"
    is IrTypeProjection -> variance.label + (if (variance != Variance.INVARIANT) " " else "") + type.asString()
    else -> error("Unexpected kind of IrTypeArgument: " + javaClass.simpleName)
}

fun IrTypeArgument.isSubtypeOf(superType: IrTypeArgument, irBuiltIns: IrBuiltIns): Boolean {
    this.typeOrNull ?: error("Can not get type from IrTypeArgument: ${this.asString()}")
    superType.typeOrNull ?: error("Can not get type from IrTypeArgument: ${superType.asString()}")
    return this.typeOrNull!!.isSubtypeOf(superType.typeOrNull!!, irBuiltIns)
}

fun IrFunction.isSubTypeOf(other: IrFunction, builtIns: IrBuiltIns) = this.wrappedIrType().isSubTypeOf(other.wrappedIrType(), builtIns)

fun IrFunction.isSubTypeOf(type: IrType, pluginContext: IrPluginContext) = this.irType().isSubtypeOf(type, this.createIrBuiltIns(pluginContext))

data class WrappedIrType(
    val irType: IrSimpleType,
    val dispatchReceiver: IrTypeProjection?,
    val extensionReceiver: IrTypeProjection?,
    val returnType: IrTypeProjection,
) {
    fun isSubTypeOf(other: WrappedIrType, builtIns: IrBuiltIns): Boolean {
        // TODO: compare classifiers types
        // TODO: check if returnType != other.returnType works correctly for all cases
        if (irType.hasDifferentArgumentsWith(other.irType) || !hasIncompatibleRecievers(other, builtIns) || returnType != other.returnType) {
            return false
        }
        return irType.arguments.zip(other.irType.arguments).all { (thisArgument, otherArgument) ->
            thisArgument.isSubtypeOf(otherArgument, builtIns)
        }
    }

    private fun IrSimpleType.hasDifferentArgumentsWith(other: IrSimpleType) = this.arguments.size != other.arguments.size

    // TODO: add KDoc
    private fun hasIncompatibleRecievers(other: WrappedIrType, builtIns: IrBuiltIns): Boolean {
        if (!this.dispatchReceiver.isReceiverSubtypeOf(other.dispatchReceiver, builtIns)) {
            return false
        }
        if (!this.extensionReceiver.isReceiverSubtypeOf(other.extensionReceiver, builtIns)) {
            return false
        }
        return true
    }

    // TODO: add KDoc
    // If two receivers are null return true.  If two receivers are NOT null check subtyping. Otherwise return false
    private fun IrTypeProjection?.isReceiverSubtypeOf(other: IrTypeProjection?, builtIns: IrBuiltIns): Boolean {
        return this?.let { thisReciever ->
            other?.let { otherReciever ->
                thisReciever.type.isSubtypeOf(otherReciever.type, builtIns)
            } ?: false
        } ?: other?.let { false } ?: true
    }
}

fun IrFunction.wrappedIrType(shouldUseVarargType: Boolean = false): WrappedIrType {
    val dispatchReceiver = this.dispatchReceiverParameter?.type?.makeTypeProjection()
    val extensionReceiver = this.extensionReceiverParameter?.type?.makeTypeProjection()
    val returnType = this.returnType.makeTypeProjection()
    return WrappedIrType(this.irType(shouldUseVarargType) as IrSimpleType, dispatchReceiver, extensionReceiver, returnType)
}

// TODO: KDoc
// Currently we store only arguments here
fun IrFunction.irType(shouldUseVarargType: Boolean = false): IrType {
    val arguments = if (shouldUseVarargType) {
        valueParameters.map { it.varargElementType ?: it.type }
    } else {
        valueParameters.map { it.type }
    }
    val projections = arguments.map { it.makeTypeProjection() }
    return IrSimpleTypeImpl(
        // TODO: get the right classifier of the function, but currently we don't take into account this field
        this.parentAsClass.symbol,
        hasQuestionMark = false,
        arguments = projections,
        annotations = emptyList(),
    )
}

fun IrType.makeTypeProjection() = makeTypeProjection(this, if (this is IrTypeProjection) this.variance else Variance.INVARIANT)

fun IrFunction.toFunctionInfo(): IrFunctionInfo =
    IrFunctionInfo(
        fqName.toString(),
        receiverFqName = receiverType()?.classFqName?.asString(),
        isObjectReceiver = receiverType()?.getClass()?.isObject ?: false,
    )

fun IrFunction.receiverType(): IrType? {
    val extensionReceiver = this.extensionReceiverParameter
    val dispatchReceiver = this.dispatchReceiverParameter
    return if (dispatchReceiver != null && dispatchReceiver !is TransientReceiver) {
        dispatchReceiver.type
    } else if (extensionReceiver != null && extensionReceiver !is TransientReceiver) {
        extensionReceiver.type
    } else {
        null
    }
}
