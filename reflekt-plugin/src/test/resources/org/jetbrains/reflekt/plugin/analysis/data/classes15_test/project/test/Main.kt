package org.jetbrains.reflekt.test

import org.jetbrains.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSupertype<B2>().withAnnotations<B2>(FirstAnnotationTest::class)
}
