package io.reflekt.resources.io.reflekt.plugin.analysis.data.functions1test.project.test

import io.reflekt.Reflekt

fun main() {
    val functions = Reflekt.functions().withAnnotations<() -> Unit>()
}
