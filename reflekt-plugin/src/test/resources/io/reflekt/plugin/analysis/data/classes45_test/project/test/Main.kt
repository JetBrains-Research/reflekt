package io.reflekt.resources.io.reflekt.plugin.analysis.data.classes45test.project.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withAnnotations<BInterfaceTest>(SecondAnnotationTest::class).withSupertypes(BInterfaceTest::class)
}
