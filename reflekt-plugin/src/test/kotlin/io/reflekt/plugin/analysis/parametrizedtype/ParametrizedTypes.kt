package io.reflekt.plugin.analysis.parametrizedtype

import io.reflekt.plugin.analysis.models.ParameterizedType
import io.reflekt.plugin.analysis.models.ParameterizedTypeVariance

// Nothing
val nothingPT = ParameterizedType(
    fqName = "kotlin.Nothing",
    superTypes = mutableSetOf(),
    parameters = emptyList(),
    variance = ParameterizedTypeVariance.INVARIANT,
    nullable = false
)

// Any
val anyPT = ParameterizedType(
    fqName = "kotlin.Any",
    superTypes = mutableSetOf(),
    parameters = emptyList(),
    variance = ParameterizedTypeVariance.INVARIANT,
    nullable = false
)

// *
val starPT = ParameterizedType.STAR

//val serializablePT = ParameterizedType(
//    fqName = "java.io.Serializable",
//    superTypes =
//)
//
//// Number
//val numberPT = ParameterizedType(
//    fqName = "kotlin.Number",
//    superTypes = setOf("java.io.Serializable", "kotlin.Any"),
//    parameters = emptyList(),
//    variance = ParameterizedTypeVariance.INVARIANT,
//    nullable = false
//)
//
//// Int
//val intPT = ParameterizedType(
//    fqName = "kotlin.Int",
//    superTypes = setOf("kotlin.Number", "kotlin.Comparable", "java.io.Serializable", "kotlin.Any", "kotlin.Int"),
//    parameters = emptyList(),
//    variance = ParameterizedTypeVariance.INVARIANT,
//    nullable = false
//)
//
//
//// We need to check if we can replace star in parameters depending on the type implementation,
//// For detailed explanation see https://kotlinlang.org/docs/generics.html#star-projections
//// So Type<in T, out T, T> with parameters Type<*, *, *> becomes Type<in Nothing, out Any?, *>
//fun ParameterizedType.replaceStar(vararg parameters: Pair<ParameterizedType, ParameterizedTypeVariance>): ParameterizedType {
//    val newParameters = this.parameters.toMutableList()
//    for ((i, p) in parameters.withIndex()) {
//        if (p.first == starPT) {
//            val newParameter = when(p.second) {
//                ParameterizedTypeVariance.IN -> nothingPT.withVariance(ParameterizedTypeVariance.IN)
//                ParameterizedTypeVariance.OUT -> anyPT.withVariance(ParameterizedTypeVariance.OUT).nullable()
//                ParameterizedTypeVariance.INVARIANT -> starPT
//                ParameterizedTypeVariance.STAR -> error("Type cannot have star as variance in implementation")
//            }
//            newParameters[i] = newParameter
//        }
//    }
//    return copy(parameters=newParameters)
//}
//
//fun ParameterizedType.outType(parameter: ParameterizedType) =
//    if (parameter == starPT) {
//        // let the implementation be OutType<out T>, so OutType<*> is equal to OutType<out Any?>, therefore we can transform it
//        // It's important to transform it to the type without star, because otherwise we cannot determine the variance of parameter
//        copy(parameters = listOf(anyPT.withVariance(ParameterizedTypeVariance.OUT).nullable()))
//    } else this
//
//
//fun ParameterizedType.inType(parameter: ParameterizedType) =
//    if (parameter == starPT) {
//        // let the implementation be InType<in T>, so InType<*> is equal to InType<in Nothing>, therefore we can transform it
//        // It's important to transform it to the type without star, because otherwise we cannot determine the variance of parameter
//        copy(parameters = listOf(nothingPT.withVariance(ParameterizedTypeVariance.IN)))
//    } else this
//
//
//// List<parameter>
//fun listPT(parameter: ParameterizedType) =
//    ParameterizedType(
//        fqName = "kotlin.collections.List",
//        superTypes = setOf("kotlin.collections.Collection", "kotlin.collections.Iterable", "kotlin.Any", "kotlin.collections.List"),
//        // List has implementation List<out T>, so we need to change the variance of parameter
//        parameters = listOf(parameter.withVariance(ParameterizedTypeVariance.OUT)),
//        variance = ParameterizedTypeVariance.INVARIANT,
//        nullable = false
//    ).replaceStar(parameter to ParameterizedTypeVariance.OUT)
//
//
//// Array<parameter>
//fun arrayPT(parameter: ParameterizedType) = ParameterizedType(
//    fqName = "kotlin.Array",
//    superTypes = setOf("kotlin.Any", "kotlin.Cloneable", "java.io.Serializable", "kotlin.Array"),
//    // Array has implementation Array<T>, so there is no need to change the variance of parameter
//    parameters = listOf(parameter),
//    variance = ParameterizedTypeVariance.INVARIANT,
//    nullable = false
//).replaceStar(parameter to ParameterizedTypeVariance.INVARIANT)
//
//// Just for example
//class InType<in T>
//
//// InType<parameter>
//fun inTypePT(parameter: ParameterizedType) = ParameterizedType(
//    fqName = "InType",
//    superTypes = setOf("kotlin.Any", "kotlin.InType"),
//    // InType has implementation InType<in T>, so we need to change the variance of parameter
//    parameters = listOf(parameter.withVariance(ParameterizedTypeVariance.IN)),
//    variance = ParameterizedTypeVariance.INVARIANT,
//    nullable = false
//).replaceStar(parameter to ParameterizedTypeVariance.IN)
//
//
//// Pair<parameter1, parameter2>
//fun pairPT(parameter1: ParameterizedType, parameter2: ParameterizedType) = ParameterizedType(
//    fqName = "kotlin.Pair",
//    superTypes = setOf("java.io.Serializable", "kotlin.Any", "kotlin.Pair"),
//    // Pair has implementation Pair<out A, out B>
//    parameters = listOf(parameter1, parameter2).map { it.withVariance(ParameterizedTypeVariance.OUT) },
//    variance = ParameterizedTypeVariance.INVARIANT,
//    nullable = false
//).replaceStar(parameter1 to ParameterizedTypeVariance.OUT, parameter2 to ParameterizedTypeVariance.OUT)
//

open class A<in T, out S, U>

open class B<R> : A<R, R, R>()

class C : B<Int>()

// надо хранить, что C наследуется от B c такими параметрами (параметры не подставлять), то в свою очередь наследуется от A вот с такими параметрами
// и когда мы туда везде подставим Int, у нас получится, что супертипами являются B<Int>, A<in Int, out Int, Int>, Any
// поэтому по сути мы можем использовать C везде, где можно вставить один из этих суперклассов или здвездочку?

class D : B<D>()

// Int : Any, Cloneable, Number, Comparable<Int>, но хранить я буду только конструкторы без подстановки еще
// и если мы посдтавим все типы, то получится как раз вот то, что наверху
// надо только супертипы хранить видимо непосредственные?
// надо разобраться как это устроено котлинеее



