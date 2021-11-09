package io.reflekt.resources.io.reflekt.plugin.analysis.data.classes45_test.project.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withAnnotations<BInterfaceTest>(SecondAnnotationTest::class).withSupertypes(BInterfaceTest::class)
}
