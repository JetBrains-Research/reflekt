package org.jetbrains.reflekt.test

import org.jetbrains.reflekt.SmartReflekt

fun main() {
    val smartClasses = SmartReflekt.classes<AInterface1Test>().filter { it.isInterface() }.resolve()
}
