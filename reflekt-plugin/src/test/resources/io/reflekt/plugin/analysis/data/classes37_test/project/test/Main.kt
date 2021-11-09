package io.reflekt.resources.io.reflekt.plugin.analysis.data.classes37_test.project.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withAnnotations<BInterfaceTest>(SecondAnnotationTest::class, FirstAnnotationTest::class).withSupertype<BInterfaceTest>()
}
