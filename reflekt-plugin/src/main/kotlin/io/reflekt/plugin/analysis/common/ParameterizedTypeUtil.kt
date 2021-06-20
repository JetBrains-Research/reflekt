package io.reflekt.plugin.analysis.common

import io.reflekt.plugin.analysis.models.ParameterizedType
import io.reflekt.plugin.analysis.models.ParameterizedTypeVariance

fun functionNParameterizedType(argumentTypes: List<ParameterizedType>, returnType: ParameterizedType): ParameterizedType {
    val fqName = "kotlin.Function${argumentTypes.size}"
    return ParameterizedType(
        fqName = fqName,
        superTypes = mutableSetOf(),
//        superTypes = setOf(fqName, Function::class.qualifiedName!!, Any::class.qualifiedName!!),
//      вот это выглядит странно, там правда так происходит?
        parameters = argumentTypes.map { it.withVariance(ParameterizedTypeVariance.IN) }.plus(returnType.withVariance(ParameterizedTypeVariance.OUT))
    )
}

//fun functionParametrizedType() = ParameterizedType(
//
//)

fun anyParametrizedType() = ParameterizedType(
    fqName = Any::class.qualifiedName!!,
    superTypes = mutableSetOf()
)

// подумать, куда лучше это перенести?
fun unitParameterizedType() = ParameterizedType(
    fqName = Unit::class.qualifiedName!!,
    superTypes = mutableSetOf(anyParametrizedType()),
)

// надо еще хранить супертипы параметров, видимо?
fun ParameterizedType.matchInto(destination: ParameterizedType, strict: Boolean = false): Boolean {
    fun matchFqName(): Boolean {
        return destination.fqName in superTypes.map { it.fqName }
    }

    //    TODO: чекнуть что количество параметров вообще важно - видимо придется хранить тип, от которого оно наследуется
    fun matchParameters(): Boolean {
        return parameters.zip(destination.parameters).all { (src, dst) ->
                src.matchInto(dst)
        }
    }

    fun matchVariance(): Boolean {
        // src into dst:
        // IN  into IN  = OK
        // IN  into INV = ERROR
        // IN  into OUT = ERROR
        // OUT into OUT = OK
        // OUT into INV = ERROR
        // OUT into IN  = ERROR
        // INV into INV = OK
        // INV into OUT = OK
        // INV into IN  = OK
        return variance == ParameterizedTypeVariance.INVARIANT || variance == destination.variance
    }

    fun matchNullable(): Boolean {
        // src into  dst:
        // T   into  T   = OK
        // T   into  T?  = OK
        // T?  into  T?  = OK
        // T?  into  T   = ERROR
        return !nullable || destination.nullable
    }

    // We can only have star in case of Clazz<T> (if it's Clazz<in T> -> Clazz<in Nothing>, if it's Clazz<out T> -> Clazz<out Any?>),
    // so if destination is star we can make sure it matches with anything
    return destination == ParameterizedType.STAR || matchFqName() && matchVariance() && matchParameters() && matchNullable()


//    return when (destination.variance) {
////        тут не надо проверять имена вообще??
//        ParameterizedTypeVariance.STAR -> true
//        ParameterizedTypeVariance.IN -> {
//            if (variance == ParameterizedTypeVariance.STAR
//                || variance == ParameterizedTypeVariance.OUT
//                || !destination.superTypes.contains(fqName)
//                || (destination.nullable && !nullable)) {
//                false
//            } else {
//                // FIXME it works only if parameters of one type is prefix for parameters of another type
//                parameters.zip(destination.parameters).all { (sourceParameter, destParameter) ->
//                    destParameter.matchInto(sourceParameter, strict = true)
//                }
//            }
//        }
//        ParameterizedTypeVariance.OUT -> {
//            if (variance == ParameterizedTypeVariance.STAR
//                || variance == ParameterizedTypeVariance.IN
//                || !superTypes.contains(destination.fqName)
//                || (nullable && !destination.nullable)) {
//                false
//            } else {
//                // FIXME it works only if parameters of one type is prefix for parameters of another type
//                parameters.zip(destination.parameters).all { (sourceParameter, destParameter) ->
//                    sourceParameter.matchInto(destParameter, strict = true)
//                }
//            }
//        }
//        ParameterizedTypeVariance.INVARIANT -> {
//            if (strict) {
//                this == destination
//            } else {
//                matchInto(destination.withVariance(ParameterizedTypeVariance.OUT))
//            }
//        }
//    }
}
