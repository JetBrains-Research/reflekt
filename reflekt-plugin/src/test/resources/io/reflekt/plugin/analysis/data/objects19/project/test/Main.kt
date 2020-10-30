package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSubType<AInterfaceTest>().withAnnotations<AInterfaceTest>(SecondAnnotationTest::class)
}
