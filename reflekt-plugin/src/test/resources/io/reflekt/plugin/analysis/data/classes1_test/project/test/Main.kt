package io.reflekt.resources.io.reflekt.plugin.analysis.data.classes1_test.project.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSupertype<B1>()
}
