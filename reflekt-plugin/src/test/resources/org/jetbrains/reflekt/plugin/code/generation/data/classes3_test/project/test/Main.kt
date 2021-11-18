package org.jetbrains.reflekt.codegen.test

import org.jetbrains.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSuperTypes(AInterfaceTest::class, BInterfaceTest::class)
    val classes1 = Reflekt.classes().withSuperType<Any>()
}
