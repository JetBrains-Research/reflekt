package org.jetbrains.reflekt.test

import org.jetbrains.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSuperTypes(BInterfaceTest::class)
    val classes1 = Reflekt.classes().withSuperType<BInterfaceTest>()
}
