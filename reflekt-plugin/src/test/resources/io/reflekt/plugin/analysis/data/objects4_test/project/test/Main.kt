package io.reflekt.resources.io.reflekt.plugin.analysis.data.objects4test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSupertype<AInterfaceTest>()
}
