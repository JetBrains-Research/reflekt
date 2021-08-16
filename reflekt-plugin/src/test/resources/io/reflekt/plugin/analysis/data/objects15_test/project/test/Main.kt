package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSupertype<A3>().withAnnotations<A3>(FirstAnnotationTest::class)
}
