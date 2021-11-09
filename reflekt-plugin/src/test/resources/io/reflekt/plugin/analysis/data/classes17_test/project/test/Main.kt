package io.reflekt.resources.io.reflekt.plugin.analysis.data.classes17_test.project.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSupertype<B1>().withAnnotations<B1>(FirstAnnotationTest::class, SecondAnnotationTest::class)
}
