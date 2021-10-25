package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSuperTypes().withAnnotations<B1>()
}
