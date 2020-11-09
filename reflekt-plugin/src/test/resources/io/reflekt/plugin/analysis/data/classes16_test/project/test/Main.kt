package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSubType<B2>().withAnnotations<B2>(FirstAnnotationTest::class, SecondAnnotationTest::class)
}
