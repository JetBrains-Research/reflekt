package io.reflekt.plugin.analysis.common

import io.reflekt.plugin.analysis.models.ParameterizedType
import io.reflekt.plugin.analysis.models.ParameterizedTypeVariance

fun functionNParameterizedType(argumentTypes: List<ParameterizedType>, returnType: ParameterizedType): ParameterizedType {
    val fqName = "kotlin.Function${argumentTypes.size}"
    return ParameterizedType(
        fqName = fqName,
        superTypeFqNames = setOf(fqName, Function::class.qualifiedName!!, Any::class.qualifiedName!!),
        parameters = argumentTypes.map { it.withVariance(ParameterizedTypeVariance.IN) }.plus(returnType.withVariance(ParameterizedTypeVariance.OUT))
    )
}

fun unitParameterizedType() = ParameterizedType(
    fqName = Unit::class.qualifiedName!!,
    superTypeFqNames = setOf(Unit::class.qualifiedName!!, Any::class.qualifiedName!!),
)

fun ParameterizedType.matchInto(destination: ParameterizedType, strict: Boolean = false): Boolean {
    return when (destination.variance) {
//        тут не надо проверять имена вообще??
        ParameterizedTypeVariance.STAR -> true
        ParameterizedTypeVariance.IN -> {
            if (variance == ParameterizedTypeVariance.STAR
                || variance == ParameterizedTypeVariance.OUT
                || !destination.superTypeFqNames.contains(fqName)
                || (destination.nullable && !nullable)) {
                false
            } else {
                // FIXME it works only if parameters of one type is prefix for parameters of another type
                parameters.zip(destination.parameters).all { (sourceParameter, destParameter) ->
                    destParameter.matchInto(sourceParameter, strict = true)
                }
            }
        }
        ParameterizedTypeVariance.OUT -> {
            if (variance == ParameterizedTypeVariance.STAR
                || variance == ParameterizedTypeVariance.IN
                || !superTypeFqNames.contains(destination.fqName)
                || (nullable && !destination.nullable)) {
                false
            } else {
                // FIXME it works only if parameters of one type is prefix for parameters of another type
                parameters.zip(destination.parameters).all { (sourceParameter, destParameter) ->
                    sourceParameter.matchInto(destParameter, strict = true)
                }
            }
        }
        ParameterizedTypeVariance.INVARIANT -> {
            if (strict) {
                this == destination
            } else {
                matchInto(destination.withVariance(ParameterizedTypeVariance.OUT))
            }
        }
    }
}
