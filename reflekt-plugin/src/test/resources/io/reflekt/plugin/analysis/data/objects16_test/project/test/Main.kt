package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSubType<A3>().withAnnotations<A3>(FirstAnnotationTest::class, SecondAnnotationTest::class)
}
