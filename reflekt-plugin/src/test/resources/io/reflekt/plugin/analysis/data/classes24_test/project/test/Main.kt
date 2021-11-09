package io.reflekt.resources.io.reflekt.plugin.analysis.data.classes24test.project.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSupertypes(B3::class).withAnnotations<B3>(FirstAnnotationTest::class)
}
