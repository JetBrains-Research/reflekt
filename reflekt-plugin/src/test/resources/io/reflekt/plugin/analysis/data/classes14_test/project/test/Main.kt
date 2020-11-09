package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSubType<B1>().withAnnotations<B1>(FirstAnnotationTest::class)
}
