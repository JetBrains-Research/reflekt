package io.reflekt.resources.io.reflekt.plugin.analysis.data.functions6_test.project.test

import io.reflekt.Reflekt

fun main() {
    val functions = Reflekt.functions().withAnnotations<Function0<*>>(FunctionsTestAnnotation::class)
}
