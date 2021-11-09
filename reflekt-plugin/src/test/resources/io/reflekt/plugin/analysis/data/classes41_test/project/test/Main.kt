package io.reflekt.resources.io.reflekt.plugin.analysis.data.classes41test.project.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withAnnotations<B3>(FirstAnnotationTest::class).withSupertypes(B3::class)
}
