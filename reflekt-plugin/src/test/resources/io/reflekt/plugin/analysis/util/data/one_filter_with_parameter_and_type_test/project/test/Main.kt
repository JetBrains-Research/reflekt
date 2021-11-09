package io.reflekt.resources.io.reflekt.plugin.analysis.util.data.one_filter_with_parameter_and_type_test.project.test

import io.reflekt.SmartReflekt
import org.jetbrains.kotlin.psi.KtClass

fun main() {
    val smartClasses = SmartReflekt.classes<AInterface1Test>().filter { klass: KtClass -> klass.isInterface() }.resolve()
}
