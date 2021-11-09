package io.reflekt.resources.io.reflekt.plugin.analysis.data.classes6_test.project.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSupertypes(BInterfaceTest::class)
    val classes1 = Reflekt.classes().withSupertype<BInterfaceTest>()
}
