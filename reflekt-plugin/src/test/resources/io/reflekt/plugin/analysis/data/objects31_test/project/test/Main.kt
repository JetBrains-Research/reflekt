package io.reflekt.resources.io.reflekt.plugin.analysis.data.objects31test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withAnnotations<A1>(FirstAnnotationTest::class).withSupertype<A1>()
}
