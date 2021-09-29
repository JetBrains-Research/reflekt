package org.jetbrains.reflekt.test

import org.jetbrains.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withAnnotations<B3>(FirstAnnotationTest::class).withSupertype<B3>()
}
