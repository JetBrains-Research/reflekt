package io.reflekt.resources.io.reflekt.plugin.analysis.data.classes22_test.project.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSupertypes(B1::class).withAnnotations<B1>()
}
