package io.reflekt.test

import io.reflekt.Reflekt

fun test() {
    val objects = Reflekt.objects().withSubType<A1>()
}
