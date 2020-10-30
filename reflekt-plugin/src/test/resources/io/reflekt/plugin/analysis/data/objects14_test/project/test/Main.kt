package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSubType<A1>().withAnnotations<A1>(FirstAnnotationTest::class)
}
