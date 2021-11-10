package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSupertypes(AInterfaceTest::class)
    val objects1 = Reflekt.objects().withSupertype<AInterfaceTest>()
}
