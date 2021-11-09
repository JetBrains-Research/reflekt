package io.reflekt.resources.io.reflekt.plugin.analysis.data.classes6test.project.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSupertypes(BInterfaceTest::class)
    val classes1 = Reflekt.classes().withSupertype<BInterfaceTest>()
}
