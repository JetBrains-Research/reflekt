package io.reflekt.resources.io.reflekt.plugin.analysis.data.objects12_test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withAnnotations<AInterfaceTest>(FirstAnnotationTest::class, SecondAnnotationTest::class)
}
