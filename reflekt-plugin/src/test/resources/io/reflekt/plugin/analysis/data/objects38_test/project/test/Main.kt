package io.reflekt.resources.io.reflekt.plugin.analysis.data.objects38test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withAnnotations<A1>().withSupertypes()
}
