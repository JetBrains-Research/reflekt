package io.reflekt.plugin.analysis.ir

import io.reflekt.plugin.analysis.common.functionNParameterizedType
import io.reflekt.plugin.analysis.models.ParameterizedType
import io.reflekt.plugin.analysis.models.ParameterizedTypeVariance
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.isObject
import org.jetbrains.kotlin.ir.util.superTypes
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

fun Variance.toParameterizedTypeVariance() = when (this) {
    Variance.INVARIANT -> ParameterizedTypeVariance.INVARIANT
    Variance.IN_VARIANCE -> ParameterizedTypeVariance.IN
    Variance.OUT_VARIANCE -> ParameterizedTypeVariance.OUT
}

fun IrType.toParameterizedType(): ParameterizedType {
    require(this is IrSimpleType)
    val variances = (getClass()?.typeParameters ?: emptyList()).map { it.variance }
    return ParameterizedType(
        fqName = classFqName.toString(),
        superTypeFqNames = superTypeFqNames(),
        parameters = arguments.zip(variances).map { (argument, variance) -> argument.toParameterizedType().withVariance(variance.toParameterizedTypeVariance()) },
        nullable = isNullable()
    )
}

fun IrTypeArgument.toParameterizedType(): ParameterizedType =
    when (this) {
        is IrStarProjection -> ParameterizedType.STAR
        is IrTypeProjection -> type.toParameterizedType().withVariance(variance.toParameterizedTypeVariance())
        else -> error("Unknown IrTypeArgument")
    }

fun IrFunction.receiver(): IrClass? = dispatchReceiverParameter?.type?.getClass() ?: extensionReceiverParameter?.type?.getClass()

fun IrFunction.isObjectReceiver(): Boolean = receiver()?.isObject ?: false

fun IrFunction.getSignature(): ParameterizedType {
    val receiverType = if (isObjectReceiver()) {
        emptyList()
    } else {
        listOfNotNull(
            dispatchReceiverParameter?.type?.toParameterizedType(),
            dispatchReceiverParameter?.type?.toParameterizedType()
        ).take(1)
    }
    val argumentTypes = receiverType.plus(valueParameters.map { it.type.toParameterizedType() })
    val returnType = this.returnType.toParameterizedType()

    return functionNParameterizedType(argumentTypes, returnType)
}
