package io.reflekt.resources.io.reflekt.plugin.analysis.data.classes15_test.project.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSupertype<B2>().withAnnotations<B2>(FirstAnnotationTest::class)
}
