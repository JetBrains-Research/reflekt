package io.reflekt.test

import com.google.devtools.ksp.symbol.ClassKind
import io.reflekt.SmartReflekt

fun main() {
    val smartClasses = SmartReflekt.classes<AInterface1Test>().filter { it.classKind == ClassKind.INTERFACE }.resolve()
}
