package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withAnnotations<AInterfaceTest>(SecondAnnotationTest::class, FirstAnnotationTest::class).withSubTypes(AInterfaceTest::class)
}
