package io.reflekt.resources.io.reflekt.plugin.analysis.data.objects42_test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withAnnotations<A3>(FirstAnnotationTest::class, SecondAnnotationTest::class).withSupertypes(A3::class)
}
