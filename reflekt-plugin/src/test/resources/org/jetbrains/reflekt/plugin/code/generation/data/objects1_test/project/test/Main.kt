package org.jetbrains.reflekt.codegen.test

import org.jetbrains.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSuperTypes(AInterfaceTest::class)
}
