package org.jetbrains.reflekt.test

import org.jetbrains.reflekt.SmartReflekt
import org.jetbrains.kotlin.psi.KtClass

fun main() {
    val smartClasses = SmartReflekt.classes<AInterface1Test>().filter { klass: KtClass -> klass.isInterface() }.resolve()
}
