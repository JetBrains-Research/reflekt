package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSubTypes(BInterfaceTest::class)
    val classes1 = Reflekt.classes().withSubType<BInterfaceTest>()
}
