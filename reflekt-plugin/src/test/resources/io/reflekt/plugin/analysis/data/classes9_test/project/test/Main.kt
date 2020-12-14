package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSubTypes(AInterfaceTest::class, BInterfaceTest::class)
    val classes1 = Reflekt.classes().withSubType<Any>()
}
