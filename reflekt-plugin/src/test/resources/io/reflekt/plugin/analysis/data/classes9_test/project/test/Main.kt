package io.reflekt.resources.io.reflekt.plugin.analysis.data.classes9_test.project.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSupertypes(AInterfaceTest::class, BInterfaceTest::class)
    val classes1 = Reflekt.classes().withSupertype<Any>()
}
