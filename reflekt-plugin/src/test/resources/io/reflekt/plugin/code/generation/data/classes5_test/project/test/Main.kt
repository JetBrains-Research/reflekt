package io.reflekt.codegen.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSuperTypes(AInterfaceTest::class, BInterfaceTest::class)
    val classes1 = Reflekt.classes().withSuperType<Any>()
    val classes2 = Reflekt.classes().withSuperTypes(AInterfaceTest::class, BInterfaceTest::class).withAnnotations<AInterfaceTest>(FirstAnnotationTest::class)
    val classes3 = Reflekt.classes().withAnnotations<Any>(FirstAnnotationTest::class).withSupertype<Any>()
}
