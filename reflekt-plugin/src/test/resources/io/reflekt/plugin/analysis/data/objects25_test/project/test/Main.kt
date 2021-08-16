package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSupertypes(A3::class).withAnnotations<A3>(FirstAnnotationTest::class, SecondAnnotationTest::class)
}
