package io.reflekt.test

import io.reflekt.SmartReflekt
import org.jetbrains.kotlin.psi.KtClass

fun main() {
    val smartClasses = SmartReflekt.classes<AInterface1Test>()
        .filter { it.isInterface() }
        .filter { true }
        .filter { klass: KtClass -> klass.isInterface() }
        .filter { klass -> klass.isInterface() }
        .resolve()
}
