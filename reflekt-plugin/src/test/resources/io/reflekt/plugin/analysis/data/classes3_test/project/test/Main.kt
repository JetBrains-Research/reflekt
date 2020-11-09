package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSubTypes(B1::class)
    val classes1 = Reflekt.classes().withSubType<B1>()
}
