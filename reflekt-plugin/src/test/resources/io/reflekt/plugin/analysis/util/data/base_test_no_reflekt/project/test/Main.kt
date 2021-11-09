package io.reflekt.resources.io.reflekt.plugin.analysis.util.data.base_test_no_reflekt.project.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSupertype<B1>().withAnnotations<B1>(FirstAnnotationTest::class, SecondAnnotationTest::class)
}
