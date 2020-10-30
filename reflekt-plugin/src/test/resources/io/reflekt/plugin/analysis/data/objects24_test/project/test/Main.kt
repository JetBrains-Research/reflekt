package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSubTypes(A3::class).withAnnotations<A3>(FirstAnnotationTest::class)
}
