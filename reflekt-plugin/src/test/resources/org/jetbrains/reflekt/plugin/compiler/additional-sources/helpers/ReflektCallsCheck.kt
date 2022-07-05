package org.jetbrains.reflekt.test.helpers

import kotlin.reflect.KClass
import kotlin.Function

private fun compareResults(actual: List<String>, expected: List<String>): String {
    return if (actual.sorted() == expected.sorted()) "OK"  else "Fail:\nactual: $actual\nexpected: $expected"
}

fun checkClassesCallResult(
    call: () -> List<KClass<*>>,
    expected: List<String>,
    basePackage: String? = null,
): String {
    val actual = call().map { it.qualifiedName ?: "Undefined name" }
    return extendExpectedAndCompareResults(actual, expected, basePackage)
}

fun checkObjectsCallResult(
    call: () -> List<Any>,
    expected: List<String>,
    basePackage: String? = null,
): String {
    val actual = call().map { it::class.qualifiedName ?: "Undefined name" }
    return extendExpectedAndCompareResults(actual, expected, basePackage)
}

fun checkFinctionsCallResult(
    call: () -> List<Function<*>>,
    expected: List<String>,
): String {
    val actual = call().map { it.toString() ?: "Undefined name" }
    return extendExpectedAndCompareResults(actual, expected)
}

private fun extendExpectedAndCompareResults(
    actual: List<String>,
    expected: List<String>,
    basePackage: String? = null,
): String {
    val expectedWithPackage = basePackage?.let{ expected.map{ "$basePackage.$it" } } ?: expected
    return compareResults(actual, expectedWithPackage)
}
