package io.reflekt.resources.io.reflekt.plugin.analysis.data.functions15test.project.test

import io.reflekt.Reflekt

fun main() {
    val functions = Reflekt.functions().withAnnotations<(Int, Boolean, Float) -> Any>(FunctionsTestAnnotation::class)
}
