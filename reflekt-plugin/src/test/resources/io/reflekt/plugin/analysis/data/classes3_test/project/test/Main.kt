package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSuperTypes(B1::class)
    val classes1 = Reflekt.classes().withSuperType<B1>()
}
