package org.jetbrains.reflekt.test

import org.jetbrains.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSupertype<A1>().withAnnotations<A1>()
}