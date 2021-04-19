package io.reflekt.test

import io.reflekt.SmartReflekt

fun main() {
    val smartClasses = SmartReflekt.classes<AInterface1Test>().filter { it.isInterface() }.resolve()
}
