package io.reflekt.resources.io.reflekt.plugin.analysis.data.objects5test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSupertypes(AInterfaceTest::class)
}
