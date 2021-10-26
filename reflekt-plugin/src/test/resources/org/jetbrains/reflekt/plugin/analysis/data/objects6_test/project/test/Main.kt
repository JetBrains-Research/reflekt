package org.jetbrains.reflekt.test

import org.jetbrains.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSuperTypes(AInterfaceTest::class)
    val objects1 = Reflekt.objects().withSuperType<AInterfaceTest>()
}
