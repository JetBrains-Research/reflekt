package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSuperType<B2>().withAnnotations<B2>(FirstAnnotationTest::class, SecondAnnotationTest::class)
}
