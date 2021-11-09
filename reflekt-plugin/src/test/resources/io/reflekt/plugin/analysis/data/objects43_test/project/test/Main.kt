package io.reflekt.resources.io.reflekt.plugin.analysis.data.objects43_test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withAnnotations<A1>(FirstAnnotationTest::class, SecondAnnotationTest::class).withSupertypes(A1::class)
}
