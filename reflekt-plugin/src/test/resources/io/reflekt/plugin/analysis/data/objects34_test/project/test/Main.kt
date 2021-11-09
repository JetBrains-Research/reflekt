package io.reflekt.resources.io.reflekt.plugin.analysis.data.objects34test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withAnnotations<A1>(FirstAnnotationTest::class, SecondAnnotationTest::class).withSupertype<A1>()
}
