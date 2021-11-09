package io.reflekt.resources.io.reflekt.plugin.analysis.data.objects6_test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSupertypes(AInterfaceTest::class)
    val objects1 = Reflekt.objects().withSupertype<AInterfaceTest>()
}
