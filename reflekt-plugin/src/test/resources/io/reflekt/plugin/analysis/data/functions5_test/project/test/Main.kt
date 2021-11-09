package io.reflekt.resources.io.reflekt.plugin.analysis.data.functions5test.project.test

import io.reflekt.Reflekt

fun main() {
    val functions = Reflekt.functions().withAnnotations<() -> Any?>(FunctionsTestAnnotation::class)
}
