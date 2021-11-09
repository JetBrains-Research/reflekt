package io.reflekt.resources.io.reflekt.plugin.code.generation.data.objects1test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSupertypes(AInterfaceTest::class)
}
