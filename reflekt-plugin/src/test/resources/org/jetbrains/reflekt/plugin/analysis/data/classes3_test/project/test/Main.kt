package org.jetbrains.reflekt.test

import org.jetbrains.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSupertypes(B1::class)
    val classes1 = Reflekt.classes().withSupertype<B1>()
}
