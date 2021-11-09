package io.reflekt.resources.io.reflekt.plugin.analysis.util.data.one_filter_test.project.test

import io.reflekt.SmartReflekt

fun main() {
    val smartClasses = SmartReflekt.classes<AInterface1Test>().filter { it.isInterface() }.resolve()
}
