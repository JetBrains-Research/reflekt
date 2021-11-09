package io.reflekt.resources.io.reflekt.plugin.code.generation.data.objects2test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withAnnotations<AInterfaceTest>()
}
