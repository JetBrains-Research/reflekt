package io.reflekt.resources.io.reflekt.plugin.analysis.data.objects35_test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withAnnotations<AInterfaceTest>().withSupertype<AInterfaceTest>()
}
