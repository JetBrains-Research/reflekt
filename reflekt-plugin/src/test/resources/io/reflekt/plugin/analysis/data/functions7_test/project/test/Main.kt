package io.reflekt.resources.io.reflekt.plugin.analysis.data.functions7test.project.test

import io.reflekt.Reflekt

fun main() {
    val functions = Reflekt.functions().withAnnotations<() -> Array<*>>(FunctionsTestAnnotation::class)
}
