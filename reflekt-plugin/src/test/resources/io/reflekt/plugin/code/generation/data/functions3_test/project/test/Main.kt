package io.reflekt.codegen.test

import io.reflekt.Reflekt

fun main() {
    val functions1 = Reflekt.functions().withAnnotations<() -> Unit>(FirstAnnotationTest::class)
    val functions2 = Reflekt.functions().withAnnotations<() -> Int>(FirstAnnotationTest::class)
}
