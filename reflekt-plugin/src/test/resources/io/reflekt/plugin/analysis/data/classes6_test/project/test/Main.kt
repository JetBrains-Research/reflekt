package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSuperTypes(BInterfaceTest::class)
    val classes1 = Reflekt.classes().withSuperType<BInterfaceTest>()
}
