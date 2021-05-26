package io.reflekt.plugin.analysis.ir

import io.reflekt.plugin.analysis.models.ParameterizedType
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.types.*

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
    (getValueArgument(0) as IrVararg).elements.map {
        (it as IrClassReference).classType.classFqName.toString()
    }

fun IrType.toParameterizedType(): ParameterizedType {
    require(this is IrSimpleType)
    return ParameterizedType(
        classFqName.toString(),
        arguments.map {
            require(it.typeOrNull != null)
            it.typeOrNull!!.toParameterizedType()
        }
    )
}
