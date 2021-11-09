package io.reflekt.resources.io.reflekt.plugin.analysis.util.data.onefilterwithparametertest.project.test

import io.reflekt.SmartReflekt

fun main() {
    val smartClasses = SmartReflekt.classes<AInterface1Test>().filter { klass -> klass.isInterface() }.resolve()
}
