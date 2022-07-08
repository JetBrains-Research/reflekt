package org.jetbrains.reflekt.plugin.compiler.providers.reflekt

import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoot
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.EnvironmentConfigurator
import org.jetbrains.kotlin.test.services.TestServices
import org.jetbrains.reflekt.plugin.util.ReflektClasspathProvider.REFLEKT_DSL
import org.jetbrains.reflekt.plugin.util.ReflektClasspathProvider.REFLEKT_PLUGIN
import org.jetbrains.reflekt.plugin.util.ReflektClasspathProvider.findJar
import org.jetbrains.reflekt.plugin.utils.Util.getKotlinCompilerJar

abstract class ReflektPluginProviderBase(testServices: TestServices) : EnvironmentConfigurator(testServices) {
    override fun configureCompilerConfiguration(configuration: CompilerConfiguration, module: TestModule) {
        listOf(REFLEKT_PLUGIN, REFLEKT_DSL).forEach {
            val jar = findJar(it, testServices)
            configuration.addJvmClasspathRoot(jar)
        }
        configuration.addJvmClasspathRoot(getKotlinCompilerJar())
    }
}
