package io.reflekt.resources.io.reflekt.plugin.analysis.data.objects14test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSupertype<A1>().withAnnotations<A1>(FirstAnnotationTest::class)
}
