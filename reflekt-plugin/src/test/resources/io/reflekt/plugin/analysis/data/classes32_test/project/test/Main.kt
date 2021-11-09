package io.reflekt.resources.io.reflekt.plugin.analysis.data.classes32_test.project.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withAnnotations<B3>(FirstAnnotationTest::class).withSupertype<B3>()
}
