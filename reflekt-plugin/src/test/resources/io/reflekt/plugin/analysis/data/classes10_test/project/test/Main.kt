package io.reflekt.resources.io.reflekt.plugin.analysis.data.classes10test.project.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withAnnotations<BInterfaceTest>()
}
