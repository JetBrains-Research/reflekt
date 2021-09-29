package org.jetbrains.reflekt.test

import org.jetbrains.reflekt.SmartReflekt

fun main() {
    val smartClasses = SmartReflekt.classes<AInterface1Test>().filter { klass -> klass.isInterface() }.resolve()
}
