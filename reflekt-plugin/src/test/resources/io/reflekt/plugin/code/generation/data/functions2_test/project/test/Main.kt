package io.reflekt.codegen.test

import io.reflekt.Reflekt

fun main() {
    val functions = Reflekt.functions().withAnnotations<() -> Unit>(FirstAnnotationTest::class)
}
