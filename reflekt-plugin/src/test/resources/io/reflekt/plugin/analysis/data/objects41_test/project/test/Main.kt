package io.reflekt.resources.io.reflekt.plugin.analysis.data.objects41_test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withAnnotations<A3>(FirstAnnotationTest::class).withSupertypes(A3::class)
}
