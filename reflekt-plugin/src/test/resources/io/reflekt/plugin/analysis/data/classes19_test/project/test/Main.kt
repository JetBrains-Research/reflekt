package io.reflekt.resources.io.reflekt.plugin.analysis.data.classes19_test.project.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSupertype<BInterfaceTest>().withAnnotations<BInterfaceTest>(SecondAnnotationTest::class)
}
