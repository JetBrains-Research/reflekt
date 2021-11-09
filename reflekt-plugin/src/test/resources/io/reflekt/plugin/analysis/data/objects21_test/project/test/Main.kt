package io.reflekt.resources.io.reflekt.plugin.analysis.data.objects21test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSupertypes().withAnnotations<A1>()
}
