package io.reflekt.resources.io.reflekt.plugin.analysis.data.classes4test.project.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSupertype<BInterfaceTest>()
}
