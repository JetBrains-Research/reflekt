package io.reflekt.resources.io.reflekt.plugin.analysis.util.data.onefilterwithparameterandtypetest.project.test

import io.reflekt.SmartReflekt
import org.jetbrains.kotlin.psi.KtClass

fun main() {
    val smartClasses = SmartReflekt.classes<AInterface1Test>().filter { klass: KtClass -> klass.isInterface() }.resolve()
}
