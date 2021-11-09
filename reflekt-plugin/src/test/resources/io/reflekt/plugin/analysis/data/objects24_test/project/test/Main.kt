package io.reflekt.resources.io.reflekt.plugin.analysis.data.objects24_test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSupertypes(A3::class).withAnnotations<A3>(FirstAnnotationTest::class)
}
