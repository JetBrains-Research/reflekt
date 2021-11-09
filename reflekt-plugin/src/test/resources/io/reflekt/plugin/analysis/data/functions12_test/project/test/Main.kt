package io.reflekt.resources.io.reflekt.plugin.analysis.data.functions12test.project.test

import io.reflekt.Reflekt

fun main() {
    val functions = Reflekt.functions().withAnnotations<() -> Array<in FunTest1>>(FunctionsTestAnnotation::class)
}
