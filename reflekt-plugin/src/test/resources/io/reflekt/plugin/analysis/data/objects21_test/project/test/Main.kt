package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSuperTypes().withAnnotations<A1>()
}
