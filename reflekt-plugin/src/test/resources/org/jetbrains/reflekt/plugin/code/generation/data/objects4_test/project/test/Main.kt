package org.jetbrains.reflekt.codegen.test

import org.jetbrains.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSuperType<A1>().withAnnotations<A1>(FirstAnnotationTest::class)
}
