package org.jetbrains.reflekt.test

import org.jetbrains.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSuperType<A1>().withAnnotations<A1>(FirstAnnotationTest::class, SecondAnnotationTest::class)
}
