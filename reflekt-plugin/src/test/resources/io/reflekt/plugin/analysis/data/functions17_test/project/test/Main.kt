package io.reflekt.resources.io.reflekt.plugin.analysis.data.functions17test.project.test

import io.reflekt.Reflekt

fun main() {
    val functions = Reflekt.functions().withAnnotations<(List<Int>) -> List<*>>(FunctionsTestAnnotation::class)
}
