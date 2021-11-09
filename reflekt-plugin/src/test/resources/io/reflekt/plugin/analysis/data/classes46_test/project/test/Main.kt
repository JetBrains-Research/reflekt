package io.reflekt.resources.io.reflekt.plugin.analysis.data.classes46test.project.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withAnnotations<BInterfaceTest>(SecondAnnotationTest::class, FirstAnnotationTest::class).withSupertypes(BInterfaceTest::class)
}
