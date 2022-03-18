@file:OptIn(ObsoleteDescriptorBasedAPI::class)

package org.jetbrains.reflekt.plugin.analysis.ir

import org.jetbrains.reflekt.plugin.analysis.models.ir.IrFunctionInfo
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
import org.jetbrains.kotlin.ir.types.impl.makeTypeProjection
import org.jetbrains.kotlin.ir.util.*
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

fun IrClass.isSubtypeOf(type: IrType, pluginContext: IrPluginContext) = this.defaultType.isSubtypeOf(type, this.createIrBuiltIns(pluginContext))

fun IrDeclaration.createIrBuiltIns(pluginContext: IrPluginContext): IrBuiltIns {
    val symbolTable = SymbolTable(IdSignatureDescriptor(JvmManglerDesc()), pluginContext.irFactory)
    val typeTranslator = TypeTranslatorImpl(symbolTable, pluginContext.languageVersionSettings, this.module)
    return IrBuiltIns(this.module.builtIns, typeTranslator, symbolTable)
}

fun IrType.makeTypeProjection() = makeTypeProjection(this, if (this is IrTypeProjection) this.variance else Variance.INVARIANT)

fun IrFunction.toFunctionInfo(): IrFunctionInfo {
    fqNameWhenAvailable ?: error("Can not get fqname for function ${this}")
    return IrFunctionInfo(
        fqNameWhenAvailable.toString(),
        receiverFqName = receiverType()?.classFqName?.asString(),
        isObjectReceiver = receiverType()?.getClass()?.isObject ?: false,
    )
}

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
