package io.reflekt.resources.io.reflekt.plugin.analysis.data.objects39test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withAnnotations<A1>().withSupertypes(A1::class)
}
