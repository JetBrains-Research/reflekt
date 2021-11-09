package io.reflekt.resources.io.reflekt.plugin.analysis.data.objects27_test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSupertypes(AInterfaceTest::class).withAnnotations<AInterfaceTest>()
}
