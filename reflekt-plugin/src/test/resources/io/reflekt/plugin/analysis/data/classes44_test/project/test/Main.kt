package io.reflekt.resources.io.reflekt.plugin.analysis.data.classes44_test.project.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withAnnotations<BInterfaceTest>().withSupertypes(BInterfaceTest::class)
}
