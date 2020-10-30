package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSubType<BInterfaceTest>().withAnnotations<BInterfaceTest>(SecondAnnotationTest::class, FirstAnnotationTest::class)
}
