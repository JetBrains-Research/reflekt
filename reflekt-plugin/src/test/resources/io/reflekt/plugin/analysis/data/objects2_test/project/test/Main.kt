package io.reflekt.resources.io.reflekt.plugin.analysis.data.objects2test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSupertypes(A1::class)
}
