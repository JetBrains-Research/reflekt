@file:OptIn(ObsoleteDescriptorBasedAPI::class)

package org.jetbrains.reflekt.plugin.analysis.ir

import org.jetbrains.reflekt.plugin.analysis.models.ir.IrFunctionInfo
import org.jetbrains.reflekt.plugin.analysis.psi.function.isObject
import org.jetbrains.reflekt.plugin.analysis.psi.function.toParameterizedType

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.serialization.signature.IdSignatureDescriptor
import org.jetbrains.kotlin.backend.jvm.codegen.psiElement
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
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

fun IrFunction.isSubTypeOf(type: IrType, pluginContext: IrPluginContext) = this.irType().isSubtypeOf(type, this.createIrBuiltIns(pluginContext))

/**
 * We need to create IrType for IrFunction from function descriptor, but we want to take into account
 * its dispatch receiver, since the existing implementation only cares about extension receiver.
 *
 *  extension receiver
 *        v
 * fun String.fooString() { ... }               --->    Function1<String, Unit>
 *
 *
 *** Note: the function above has the same IrType as the one below ***
 * TODO: do we want to distinguish them?
 *
 * fun foo(s: String): Unit { ... }             --->    Function1<String, Unit>
 *
 *
 *    dispatch receiver
 *          v
 * class MyClass {
 *     fun fooClass() { ... }                   --->    Function1<MyClass, Unit>
 *
 *     fun String.fooStringClass() { ... }      --->    Function2<MyClass, String, Unit>
 *           ^
 *     extension receiver
 * }
 *
 * In case of having both dispatch and extension receiver, we give priority to the dispatch receiver, since such functions
 * can only be called in the scope of dispatch receiver (i.e. inside MyClass or scope functions)
 *
 * However, if function's dispatch receiver is an object (or a companion object), we ignore it,
 * since we can call the function without it:
 *
 *    dispatch receiver (ignore)
 *            v
 * object MyObject {
 *     fun fooObject() { ... }                  --->    Function0<Unit>
 *
 *
 *     fun String.fooStringObject() { ... }     --->    Function1<String, Unit>
 *          ^
 *     extension receiver
 *
 * @param shouldUseVarargType
 * @return created IR type
 */
// TODO: delete duplicate from function/Util.kt
fun IrFunction.irType(shouldUseVarargType: Boolean = false): IrType {
    // If function is inside an object (or companion object), we don't want to consider its dispatch receiver
    val dispatchReceiver = if (this.dispatchReceiverParameter?.descriptor.isObject()) {
        null
    } else {
        this.dispatchReceiverParameter
    }
    val extensionReceiver = this.extensionReceiverParameter

    val parameters = if (shouldUseVarargType) {
        valueParameters.map { it.varargElementType ?: it.type }.toMutableList()
    } else {
        valueParameters.map { it.type }.toMutableList()
    }
    // If function has both receivers, we need to add its extension receiver to its parameters
    if (dispatchReceiver != null && extensionReceiver != null) {
        parameters.add(0, extensionReceiver.type)
    }
    returnType is IrTypeProjection
    return IrSimpleTypeImpl(
        this.returnType.classifierOrFail,
        hasQuestionMark = this.returnType.isNullable(),
        arguments = parameters.map { makeTypeProjection(it, if (it is IrTypeProjection) it.variance else Variance.INVARIANT) },
        annotations = this.returnType.annotations,
    )
}

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
