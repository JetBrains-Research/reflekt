package io.reflekt.resources.io.reflekt.plugin.analysis.data.classes35test.project.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withAnnotations<BInterfaceTest>().withSupertype<BInterfaceTest>()
}
