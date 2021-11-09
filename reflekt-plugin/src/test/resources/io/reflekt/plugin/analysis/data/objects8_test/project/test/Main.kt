package io.reflekt.resources.io.reflekt.plugin.analysis.data.objects8_test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSupertypes(AInterfaceTest::class, BInterfaceTest::class)
}
