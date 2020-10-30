package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSubTypes(AInterfaceTest::class)
    val objects1 = Reflekt.objects().withSubType<AInterfaceTest>()
}
