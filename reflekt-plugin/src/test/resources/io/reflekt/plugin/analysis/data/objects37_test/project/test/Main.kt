package io.reflekt.resources.io.reflekt.plugin.analysis.data.objects37_test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withAnnotations<AInterfaceTest>(SecondAnnotationTest::class, FirstAnnotationTest::class).withSupertype<AInterfaceTest>()
}
