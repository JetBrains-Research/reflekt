package org.jetbrains.reflekt.test

import org.jetbrains.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSupertype<A3>().withAnnotations<A3>(FirstAnnotationTest::class)
}
