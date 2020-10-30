package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSubTypes(A1::class).withAnnotations<A1>(FirstAnnotationTest::class, SecondAnnotationTest::class)
}
