package io.reflekt.resources.io.reflekt.plugin.analysis.data.functions19test.project.test

import io.reflekt.Reflekt

fun main() {
    val functions = Reflekt.functions().withAnnotations<(List<String>) -> List<*>>(FunctionsTestAnnotation::class)
}
