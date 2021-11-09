package io.reflekt.resources.io.reflekt.plugin.analysis.data.classes28test.project.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSupertypes(BInterfaceTest::class).withAnnotations<BInterfaceTest>(SecondAnnotationTest::class)
}
