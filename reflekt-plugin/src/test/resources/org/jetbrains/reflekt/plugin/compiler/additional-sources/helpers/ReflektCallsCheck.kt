package org.jetbrains.reflekt.test.helpers

import kotlin.reflect.KClass
import kotlin.Function
import org.jetbrains.reflekt.ReflektClass


inline fun checkClassesCallResult(
    call: () -> Collection<ReflektClass<*>>,
    expected: Collection<ReflektClass<*>>,
): String {
    val actual = call()
    return extendExpectedAndCompareResults(actual, expected)
}

inline fun checkObjectsCallResult(
    call: () -> List<Any>,
    expected: List<String>,
    basePackage: String? = null,
): String {
    val actual = call().map { it::class.qualifiedName ?: "Undefined name" }
    return extendExpectedAndCompareResults(actual, expected, basePackage)
}

inline fun checkFunctionsCallResult(
    call: () -> List<Function<*>>,
    expected: List<String>,
): String {
    val actual = call().map { it.toString() ?: "Undefined name" }
    return extendExpectedAndCompareResults(actual, expected)
}

@PublishedApi
internal fun extendExpectedAndCompareResults(
    actual: List<String>,
    expected: List<String>,
    basePackage: String? = null,
): String {
    val expectedWithPackage = basePackage?.let { expected.map { "$basePackage.$it" } } ?: expected
    return if (actual.sorted() == expectedWithPackage.sorted()) "OK" else "Fail:\nactual: $actual\nexpected: $expectedWithPackage"
}

@PublishedApi
internal fun extendExpectedAndCompareResults(
    actual: Collection<ReflektClass<*>>,
    expected: Collection<ReflektClass<*>>,
): String = if (actual.map { it.toString() }.containsAll(expected.map { it.toString() })) "OK" else "Fail:\nactual: $actual\nexpected: $expected"
