package io.reflekt.plugin.analysis.ir

import io.reflekt.plugin.analysis.psi.function.toParameterizedType
import org.jetbrains.kotlin.backend.jvm.codegen.psiElement
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.isObject
import org.jetbrains.kotlin.ir.util.superTypes
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.KotlinType

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

private fun IrType.collectSuperTypeFqNames(set: MutableSet<String>) {
    classFqName?.let {
        set.add(it.asString())
    }
    superTypes().forEach { it.collectSuperTypeFqNames(set) }
}

fun IrType.superTypeFqNames(): Set<String> {
    val result = HashSet<String>()
    collectSuperTypeFqNames(result)
    return result
}

@ObsoleteDescriptorBasedAPI
fun IrType.toParameterizedType(): KotlinType {
    return toKotlinType()
}

fun IrFunction.receiver(): IrClass? = dispatchReceiverParameter?.type?.getClass() ?: extensionReceiverParameter?.type?.getClass()

fun IrFunction.isObjectReceiver(): Boolean = receiver()?.isObject ?: false

fun IrFunction.toParameterizedType(binding: BindingContext): KotlinType? {
    return (psiElement as? KtNamedFunction)?.toParameterizedType(binding)
}
