package io.reflekt.resources.io.reflekt.plugin.analysis.data.classes33_test.project.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withAnnotations<B3>(FirstAnnotationTest::class, SecondAnnotationTest::class).withSupertype<B3>()
}
