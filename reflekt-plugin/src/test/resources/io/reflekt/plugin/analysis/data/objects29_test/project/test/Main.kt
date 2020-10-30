package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSubTypes(AInterfaceTest::class).withAnnotations<AInterfaceTest>(SecondAnnotationTest::class, FirstAnnotationTest::class)
}
