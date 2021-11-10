package io.reflekt.codegen.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSupertypes(B1::class).withAnnotations<B1>(FirstAnnotationTest::class, SecondAnnotationTest::class)
}
