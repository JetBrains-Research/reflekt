package org.jetbrains.reflekt.example

import org.jetbrains.kotlin.ir.util.isTopLevel
import org.jetbrains.reflekt.SmartReflekt

fun main() {
    val smartFunctions = SmartReflekt.functions<() -> Unit>().filter { it.isTopLevel && it.name.asString() == "foo" }.resolve()
    println(smartFunctions)
    for (it in smartFunctions) {
        it()
    }
}
