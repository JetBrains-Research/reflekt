package org.jetbrains.reflekt.test

import org.jetbrains.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSuperTypes(B3::class).withAnnotations<B3>(FirstAnnotationTest::class, SecondAnnotationTest::class)
}
