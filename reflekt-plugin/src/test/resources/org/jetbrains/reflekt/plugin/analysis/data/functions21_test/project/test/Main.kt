package org.jetbrains.reflekt.test

import org.jetbrains.reflekt.Reflekt

fun main() {
    val functions = Reflekt.functions().withAnnotations<(Array<in FunTest2>) -> Array<out FunTest2>>(FunctionsTestAnnotation::class)
}
