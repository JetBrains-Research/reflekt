package io.reflekt.resources.io.reflekt.plugin.analysis.data.functions21_test.project.test

import io.reflekt.Reflekt

fun main() {
    val functions = Reflekt.functions().withAnnotations<(Array<in FunTest2>) -> Array<out FunTest2>>(FunctionsTestAnnotation::class)
}
