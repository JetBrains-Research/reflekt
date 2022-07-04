package org.jetbrains.reflekt.plugin.compiler.providers.reflekt

import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.RuntimeClasspathProvider
import org.jetbrains.kotlin.test.services.TestServices
import org.jetbrains.reflekt.plugin.util.ReflektClasspathProvider.REFLEKT_DSL
import org.jetbrains.reflekt.plugin.util.ReflektClasspathProvider.REFLEKT_PLUGIN
import org.jetbrains.reflekt.plugin.util.ReflektClasspathProvider.findJar
import java.io.File

class ReflektRuntimeClasspathProvider(testServices: TestServices) : RuntimeClasspathProvider(testServices) {
    override fun runtimeClassPaths(module: TestModule): List<File> {
        return listOf(REFLEKT_PLUGIN, REFLEKT_DSL).map { findJar(it, testServices) }
    }
}
