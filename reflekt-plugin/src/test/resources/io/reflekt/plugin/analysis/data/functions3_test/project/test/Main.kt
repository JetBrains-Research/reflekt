package io.reflekt.resources.io.reflekt.plugin.analysis.data.functions3_test.project.test

import io.reflekt.Reflekt

fun main() {
    val functions = Reflekt.functions().withAnnotations<() -> Unit>(FirstAnnotationTest::class, SecondAnnotationTest::class)
}
