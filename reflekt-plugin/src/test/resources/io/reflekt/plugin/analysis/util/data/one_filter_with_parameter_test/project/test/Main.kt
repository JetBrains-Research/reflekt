package io.reflekt.test

import io.reflekt.SmartReflekt

fun main() {
    val smartClasses = SmartReflekt.classes<AInterface1Test>().filter { klass -> klass.isInterface() }.resolve()
}
