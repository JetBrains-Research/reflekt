package io.reflekt.resources.io.reflekt.plugin.analysis.data.classes42_test.project.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withAnnotations<B3>(FirstAnnotationTest::class, SecondAnnotationTest::class).withSupertypes(B3::class)
}
