package io.reflekt.codegen.test

import io.reflekt.Reflekt

fun main() {
    val functions = Reflekt.functions().withAnnotations<() -> Unit>(FirstAnnotationTest::class)
    val functions1 = Reflekt.functions().withAnnotations<(Int) -> Unit>(FirstAnnotationTest::class)
}
