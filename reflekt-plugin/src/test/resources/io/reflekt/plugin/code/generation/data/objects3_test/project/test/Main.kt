package io.reflekt.resources.io.reflekt.plugin.code.generation.data.objects3test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSupertypes(AInterfaceTest::class, BInterfaceTest::class)
    val objects1 = Reflekt.objects().withSupertype<Any>()
}
