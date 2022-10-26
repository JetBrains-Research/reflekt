package org.jetbrains.reflekt.test.helpers

import kotlin.reflect.KClass
import kotlin.Function
import org.jetbrains.reflekt.*

inline fun checkClassesCallResult(
    call: () -> Collection<ReflektClass<*>>,
    expected: Collection<ReflektClass<*>>,
): String {
    val actual = call()
    return extendExpectedAndCompareResults(actual, expected)
}

inline fun checkObjectsCallResult(
    call: () -> List<ReflektClass<*>>,
    expected: List<ReflektClass<*>>,
): String = checkClassesCallResult(call, expected)

inline fun checkFunctionsCallResult(
    call: () -> List<ReflektFunction<Function<*>>>,
    expected: List<String>,
): String {
    val actual = call().map { it.toString() ?: "Undefined name" }
    return extendExpectedAndCompareResults(actual, expected)
}

@PublishedApi
internal fun extendExpectedAndCompareResults(
    actual: Collection<Any>,
    expected: Collection<Any>,
): String = if (actual.map { it.toString() }.containsAll(expected.map { it.toString() })) "OK" else "Fail:\nactual: $actual\nexpected: $expected"
