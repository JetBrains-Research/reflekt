package io.reflekt.resources.io.reflekt.plugin.analysis.data.objects11test.project.test

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withAnnotations<AInterfaceTest>(FirstAnnotationTest::class)
}
