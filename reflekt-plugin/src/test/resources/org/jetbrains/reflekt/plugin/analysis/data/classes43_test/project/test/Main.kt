package org.jetbrains.reflekt.test

import org.jetbrains.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withAnnotations<B1>(FirstAnnotationTest::class, SecondAnnotationTest::class).withSupertypes(B1::class)
}
