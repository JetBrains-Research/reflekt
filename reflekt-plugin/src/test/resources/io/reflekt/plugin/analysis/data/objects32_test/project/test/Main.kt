package io.reflekt.resources.io.reflekt.plugin.analysis.data.objects32test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withAnnotations<A3>(FirstAnnotationTest::class).withSupertype<A3>()
}
