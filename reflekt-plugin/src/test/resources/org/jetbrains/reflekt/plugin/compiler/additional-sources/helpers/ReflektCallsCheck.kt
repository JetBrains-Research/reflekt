package org.jetbrains.reflekt.test.helpers

fun compareResults(actual: List<String>, expected: List<String>): String {
    return if (actual.toSet() == expected.toSet()) "OK"  else "Fail:\nactual: $actual\nexpected: $expected"
}

fun checkCallResult(call: () -> List<Any>, expected: List<String>): String {
    val objects = call()
    val actual = objects.map { it::class.qualifiedName ?: "Undefined name" }
    return compareResults(actual, expected)
}
