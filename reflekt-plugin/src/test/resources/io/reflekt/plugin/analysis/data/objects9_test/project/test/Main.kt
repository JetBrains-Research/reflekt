package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSubTypes(AInterfaceTest::class, BInterfaceTest::class)
    val objects1 = Reflekt.objects().withSubType<Any>()
}
