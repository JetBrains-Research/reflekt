package io.reflekt.resources.io.reflekt.plugin.analysis.data.objects30_test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withAnnotations<A1>().withSupertype<A1>()
}
