package io.reflekt.resources.io.reflekt.plugin.code.generation.data.objects4_test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSupertype<A1>().withAnnotations<A1>(FirstAnnotationTest::class)
}
