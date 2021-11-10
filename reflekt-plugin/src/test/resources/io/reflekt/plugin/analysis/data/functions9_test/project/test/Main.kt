package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val functions = Reflekt.functions().withAnnotations<() -> Array<out FunTest3>>(FunctionsTestAnnotation::class)
}
