package io.reflekt.resources.io.reflekt.plugin.analysis.data.objects22test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSupertypes(A1::class).withAnnotations<A1>()
}
