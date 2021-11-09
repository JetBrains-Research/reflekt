package io.reflekt.resources.io.reflekt.plugin.analysis.data.functions4_test.project.test

import io.reflekt.Reflekt

fun main() {
    val functions = Reflekt.functions().withAnnotations<() -> Any>(FunctionsTestAnnotation::class)
}
