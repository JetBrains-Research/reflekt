package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val functions = Reflekt.functions().withAnnotations<() -> Array<in FunTest2>>(FunctionsTestAnnotation::class)
}
