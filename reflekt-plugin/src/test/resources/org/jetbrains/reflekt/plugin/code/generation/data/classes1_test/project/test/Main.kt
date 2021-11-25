package org.jetbrains.reflekt.codegen.test

import org.jetbrains.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSuperTypes(BInterfaceTest::class)
}
