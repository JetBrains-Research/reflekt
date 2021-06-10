package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val functions = Reflekt.functions().withAnnotations<Function0<*>>(FunctionsTestAnnotation::class)
}
