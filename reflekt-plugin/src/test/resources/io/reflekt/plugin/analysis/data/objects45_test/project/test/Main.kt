package io.reflekt.resources.io.reflekt.plugin.analysis.data.objects45test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withAnnotations<AInterfaceTest>(SecondAnnotationTest::class).withSupertypes(AInterfaceTest::class)
}
