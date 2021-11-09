package io.reflekt.resources.io.reflekt.plugin.analysis.data.objects13test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSupertype<A1>().withAnnotations<A1>()
}
