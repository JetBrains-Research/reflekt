package io.reflekt.resources.io.reflekt.plugin.analysis.data.functions11_test.project.test

import io.reflekt.Reflekt

fun main() {
    val functions = Reflekt.functions().withAnnotations<() -> Array<out FunTest2>>(FunctionsTestAnnotation::class)
}
