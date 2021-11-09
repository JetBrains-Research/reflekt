package io.reflekt.resources.io.reflekt.plugin.code.generation.data.functions2test.project.test

import io.reflekt.Reflekt

fun main() {
    val functions = Reflekt.functions().withAnnotations<() -> Unit>(FirstAnnotationTest::class)
}
