package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withAnnotations<A1>(FirstAnnotationTest::class, SecondAnnotationTest::class).withSubType<A1>()
}
