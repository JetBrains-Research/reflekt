package io.reflekt.resources.io.reflekt.plugin.analysis.data.classes21_test.project.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSupertypes().withAnnotations<B1>()
}
