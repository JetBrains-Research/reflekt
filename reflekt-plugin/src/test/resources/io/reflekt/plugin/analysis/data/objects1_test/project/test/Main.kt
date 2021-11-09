package io.reflekt.resources.io.reflekt.plugin.analysis.data.objects1test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSupertype<A1>()
}
