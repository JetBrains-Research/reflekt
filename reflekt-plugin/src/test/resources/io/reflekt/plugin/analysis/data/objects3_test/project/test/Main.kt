package io.reflekt.resources.io.reflekt.plugin.analysis.data.objects3test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSupertypes(A1::class)
    val objects1 = Reflekt.objects().withSupertype<A1>()
}
