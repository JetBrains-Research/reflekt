package org.jetbrains.reflekt.test

import org.jetbrains.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSuperTypes(B1::class).withAnnotations<B1>(FirstAnnotationTest::class)
}
