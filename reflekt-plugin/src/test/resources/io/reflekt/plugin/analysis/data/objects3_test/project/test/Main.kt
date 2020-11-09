package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSubTypes(A1::class)
    val objects1 = Reflekt.objects().withSubType<A1>()
}
