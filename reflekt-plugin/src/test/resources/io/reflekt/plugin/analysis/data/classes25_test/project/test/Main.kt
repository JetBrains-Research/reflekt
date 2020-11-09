package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSubTypes(B3::class).withAnnotations<B3>(FirstAnnotationTest::class, SecondAnnotationTest::class)
}
