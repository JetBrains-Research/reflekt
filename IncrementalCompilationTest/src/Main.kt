package org.jetbrains.reflekt.plugin.ic.data.base_test_with_reflekt_import

import org.jetbrains.reflekt.Reflekt

fun main() {
    val classes1 = Reflekt.classes().withSupertype<Any>().toList()
    println(classes1)
    println("Hello, World!")
}
