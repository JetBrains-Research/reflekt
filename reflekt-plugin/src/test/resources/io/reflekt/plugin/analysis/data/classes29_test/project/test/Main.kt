package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSubTypes(BInterfaceTest::class).withAnnotations<BInterfaceTest>(SecondAnnotationTest::class, FirstAnnotationTest::class)
}
