package io.reflekt.resources.io.reflekt.plugin.code.generation.data.classes3_test.project.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSupertypes(AInterfaceTest::class, BInterfaceTest::class)
    val classes1 = Reflekt.classes().withSupertype<Any>()
}
