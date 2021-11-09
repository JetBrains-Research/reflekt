package io.reflekt.resources.io.reflekt.plugin.analysis.data.functions20_test.project.test

import io.reflekt.Reflekt

fun main() {
    val functions = Reflekt.functions().withAnnotations<(Array<in FunTest1>) -> Array<out FunTest2>>(FunctionsTestAnnotation::class)
}
