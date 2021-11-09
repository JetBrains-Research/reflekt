package io.reflekt.resources.io.reflekt.plugin.analysis.data.functions13test.project.test

import io.reflekt.Reflekt

fun main() {
    val functions = Reflekt.functions().withAnnotations<() -> Array<out FunTest1>>(FunctionsTestAnnotation::class)
}
