package io.reflekt.resources.io.reflekt.plugin.analysis.data.objects19test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSupertype<AInterfaceTest>().withAnnotations<AInterfaceTest>(SecondAnnotationTest::class)
}
