package io.reflekt.resources.io.reflekt.plugin.analysis.data.classes8_test.project.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSupertypes(AInterfaceTest::class, BInterfaceTest::class)
}
