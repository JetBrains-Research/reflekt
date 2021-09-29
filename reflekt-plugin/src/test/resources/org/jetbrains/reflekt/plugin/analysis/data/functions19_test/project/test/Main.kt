package org.jetbrains.reflekt.test

import org.jetbrains.reflekt.Reflekt

fun main() {
    val functions = Reflekt.functions().withAnnotations<(List<String>) -> List<*>>(FunctionsTestAnnotation::class)
}
