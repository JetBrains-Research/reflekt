package io.reflekt.resources.io.reflekt.plugin.analysis.data.functions9test.project.test

import io.reflekt.Reflekt

fun main() {
    val functions = Reflekt.functions().withAnnotations<() -> Array<out FunTest3>>(FunctionsTestAnnotation::class)
}
