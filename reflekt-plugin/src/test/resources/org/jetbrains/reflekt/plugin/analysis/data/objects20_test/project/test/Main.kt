package org.jetbrains.reflekt.test

import org.jetbrains.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSuperType<AInterfaceTest>().withAnnotations<AInterfaceTest>(SecondAnnotationTest::class, FirstAnnotationTest::class)
}
