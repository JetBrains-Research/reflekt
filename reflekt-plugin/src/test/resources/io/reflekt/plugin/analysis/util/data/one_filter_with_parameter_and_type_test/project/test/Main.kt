package io.reflekt.test

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import io.reflekt.SmartReflekt

fun main() {
    val smartClasses = SmartReflekt.classes<AInterface1Test>().filter { klass: KSClassDeclaration -> klass.classKind == ClassKind.INTERFACE }.resolve()
}
