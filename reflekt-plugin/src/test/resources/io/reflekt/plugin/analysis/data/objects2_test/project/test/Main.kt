package io.reflekt.resources.io.reflekt.plugin.analysis.data.objects2_test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSupertypes(A1::class)
}
