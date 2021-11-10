package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val functions = Reflekt.functions().withAnnotations<(Int, Boolean, Float) -> Any>(FunctionsTestAnnotation::class)
}
