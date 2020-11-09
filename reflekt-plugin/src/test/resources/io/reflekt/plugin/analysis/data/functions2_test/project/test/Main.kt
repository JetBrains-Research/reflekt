package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val functions = Reflekt.functions().withAnnotations(FirstAnnotationTest::class)
}
