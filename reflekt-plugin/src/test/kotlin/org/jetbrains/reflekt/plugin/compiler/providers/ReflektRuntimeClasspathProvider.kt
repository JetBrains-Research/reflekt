package org.jetbrains.reflekt.plugin.compiler.providers

import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.RuntimeClasspathProvider
import org.jetbrains.kotlin.test.services.TestServices
import org.jetbrains.reflekt.plugin.compiler.providers.ReflektClasspathProvider.REFLEKT_CORE
import org.jetbrains.reflekt.plugin.compiler.providers.ReflektClasspathProvider.REFLEKT_PLUGIN
import org.jetbrains.reflekt.plugin.compiler.providers.ReflektClasspathProvider.findJar
import java.io.File

class ReflektRuntimeClasspathProvider(testServices: TestServices) : RuntimeClasspathProvider(testServices) {
    override fun runtimeClassPaths(module: TestModule): List<File> {
        return listOf(
            findJar(REFLEKT_PLUGIN, testServices),
            findJar(REFLEKT_CORE, testServices)
        )
    }
}
