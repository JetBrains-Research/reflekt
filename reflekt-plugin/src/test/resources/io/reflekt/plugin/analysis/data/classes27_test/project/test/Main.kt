package io.reflekt.resources.io.reflekt.plugin.analysis.data.classes27_test.project.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSupertypes(BInterfaceTest::class).withAnnotations<BInterfaceTest>()
}
