package io.reflekt.test

import io.reflekt.SmartReflekt
import org.jetbrains.kotlin.psi.KtClass

fun main() {
    val smartClasses = SmartReflekt.classes<AInterface1Test>().filter { klass: KtClass -> klass.isInterface() }.resolve()
}
