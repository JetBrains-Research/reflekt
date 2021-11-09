package io.reflekt.resources.io.reflekt.plugin.analysis.data.classes39test.project.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withAnnotations<B1>().withSupertypes(B1::class)
}
