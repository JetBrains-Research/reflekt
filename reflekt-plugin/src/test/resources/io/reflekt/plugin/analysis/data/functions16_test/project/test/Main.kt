package io.reflekt.resources.io.reflekt.plugin.analysis.data.functions16_test.project.test

import io.reflekt.Reflekt

fun main() {
    val functions = Reflekt.functions().withAnnotations<(List<*>) -> List<*>>(FunctionsTestAnnotation::class)
}
