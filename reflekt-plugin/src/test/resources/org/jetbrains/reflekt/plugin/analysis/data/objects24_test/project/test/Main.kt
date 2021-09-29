package org.jetbrains.reflekt.test

import org.jetbrains.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSupertypes(A3::class).withAnnotations<A3>(FirstAnnotationTest::class)
}
