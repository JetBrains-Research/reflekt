package io.reflekt.resources.io.reflekt.plugin.analysis.data.classes30_test.project.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withAnnotations<B1>().withSupertype<B1>()
}
