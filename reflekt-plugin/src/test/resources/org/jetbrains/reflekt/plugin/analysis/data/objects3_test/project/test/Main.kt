package org.jetbrains.reflekt.test

import org.jetbrains.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSuperTypes(A1::class)
    val objects1 = Reflekt.objects().withSuperType<A1>()
}
