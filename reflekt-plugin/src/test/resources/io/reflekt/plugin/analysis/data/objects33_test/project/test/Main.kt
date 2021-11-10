package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withAnnotations<A3>(FirstAnnotationTest::class, SecondAnnotationTest::class).withSupertype<A3>()
}
