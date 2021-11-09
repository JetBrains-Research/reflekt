package io.reflekt.resources.io.reflekt.plugin.analysis.data.objects46_test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withAnnotations<AInterfaceTest>(SecondAnnotationTest::class, FirstAnnotationTest::class).withSupertypes(AInterfaceTest::class)
}
