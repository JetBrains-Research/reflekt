package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val functions = Reflekt.functions().withAnnotations<(List<Int>) -> List<*>>(FunctionsTestAnnotation::class)
}
