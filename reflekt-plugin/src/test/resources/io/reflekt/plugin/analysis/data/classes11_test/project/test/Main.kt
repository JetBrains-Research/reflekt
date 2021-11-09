package io.reflekt.resources.io.reflekt.plugin.analysis.data.classes11_test.project.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withAnnotations<BInterfaceTest>(FirstAnnotationTest::class)
}
