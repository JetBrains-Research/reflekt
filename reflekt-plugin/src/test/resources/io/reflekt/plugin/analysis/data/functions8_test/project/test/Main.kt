package io.reflekt.resources.io.reflekt.plugin.analysis.data.functions8_test.project.test

import io.reflekt.Reflekt

fun main() {
    val functions = Reflekt.functions().withAnnotations<() -> Array<in FunTest3>>(FunctionsTestAnnotation::class)
}
