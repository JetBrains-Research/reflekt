package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSuperType<A1>().withAnnotations<A1>()
}
