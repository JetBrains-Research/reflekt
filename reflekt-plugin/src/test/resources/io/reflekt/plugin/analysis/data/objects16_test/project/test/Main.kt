package io.reflekt.resources.io.reflekt.plugin.analysis.data.objects16test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSupertype<A3>().withAnnotations<A3>(FirstAnnotationTest::class, SecondAnnotationTest::class)
}
