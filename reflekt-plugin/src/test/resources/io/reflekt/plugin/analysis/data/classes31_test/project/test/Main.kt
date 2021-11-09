package io.reflekt.resources.io.reflekt.plugin.analysis.data.classes31test.project.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withAnnotations<B1>(FirstAnnotationTest::class).withSupertype<B1>()
}
