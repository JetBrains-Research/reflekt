package org.jetbrains.reflekt.plugin.ic.data.base_test_with_reflekt_usage

import org.jetbrains.reflekt.Reflekt

class A

fun main() {
    val classes1 = Reflekt.classes().withSupertype<Any>().toList()
    println(classes1)
    println("Hello, World!")
}
