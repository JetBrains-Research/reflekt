package org.jetbrains.reflekt.codegen.test

import org.jetbrains.reflekt.Reflekt

fun main() {
    val functions = Reflekt.functions().withAnnotations<() -> Unit>()
}
