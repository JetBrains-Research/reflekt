package io.reflekt.resources.io.reflekt.plugin.analysis.data.functions16test.project.test

import io.reflekt.Reflekt

fun main() {
    val functions = Reflekt.functions().withAnnotations<(List<*>) -> List<*>>(FunctionsTestAnnotation::class)
}
