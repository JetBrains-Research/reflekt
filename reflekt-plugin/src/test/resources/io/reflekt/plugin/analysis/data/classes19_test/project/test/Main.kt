package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSupertype<BInterfaceTest>().withAnnotations<BInterfaceTest>(SecondAnnotationTest::class)
}
