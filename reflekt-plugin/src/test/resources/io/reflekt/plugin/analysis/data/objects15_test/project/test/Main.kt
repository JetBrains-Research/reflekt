package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSuperType<A3>().withAnnotations<A3>(FirstAnnotationTest::class)
}
