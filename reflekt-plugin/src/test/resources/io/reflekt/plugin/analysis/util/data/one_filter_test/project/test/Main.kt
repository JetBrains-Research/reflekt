package io.reflekt.resources.io.reflekt.plugin.analysis.util.data.onefiltertest.project.test

import io.reflekt.SmartReflekt

fun main() {
    val smartClasses = SmartReflekt.classes<AInterface1Test>().filter { it.isInterface() }.resolve()
}
