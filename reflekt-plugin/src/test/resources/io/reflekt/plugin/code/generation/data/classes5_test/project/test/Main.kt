package io.reflekt.codegen.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSupertypes(AInterfaceTest::class, BInterfaceTest::class)
    val classes1 = Reflekt.classes().withSupertype<Any>()
    val classes2 = Reflekt.classes().withSupertypes(AInterfaceTest::class, BInterfaceTest::class).withAnnotations<AInterfaceTest>(FirstAnnotationTest::class)
    val classes3 = Reflekt.classes().withAnnotations<Any>(FirstAnnotationTest::class).withSupertype<Any>()
}
