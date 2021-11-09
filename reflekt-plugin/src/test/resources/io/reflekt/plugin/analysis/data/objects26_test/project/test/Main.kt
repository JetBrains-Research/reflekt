package io.reflekt.resources.io.reflekt.plugin.analysis.data.objects26test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSupertypes(A1::class).withAnnotations<A1>(FirstAnnotationTest::class, SecondAnnotationTest::class)
}
