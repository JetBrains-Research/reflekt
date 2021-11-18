package org.jetbrains.reflekt.test

import org.jetbrains.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSuperType<A3>().withAnnotations<A3>(FirstAnnotationTest::class, SecondAnnotationTest::class)
}
