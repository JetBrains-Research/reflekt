package org.jetbrains.reflekt.plugin.compiler.providers.reflekt

import com.intellij.mock.MockProject
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoot
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.EnvironmentConfigurator
import org.jetbrains.kotlin.test.services.TestServices
import org.jetbrains.reflekt.plugin.ReflektComponentRegistrar
import org.jetbrains.reflekt.plugin.util.ReflektClasspathProvider.REFLEKT_DSL
import org.jetbrains.reflekt.plugin.util.ReflektClasspathProvider.REFLEKT_PLUGIN
import org.jetbrains.reflekt.plugin.utils.Util.getKotlinCompilerJar
import org.jetbrains.reflekt.plugin.util.ReflektClasspathProvider.findJar

abstract class ReflektPluginProviderBase(testServices: TestServices) : EnvironmentConfigurator(testServices) {
    override fun registerCompilerExtensions(project: Project, module: TestModule, configuration: CompilerConfiguration) {
        ReflektComponentRegistrar(true).registerProjectComponents(project as MockProject, configuration)
    }

    override fun configureCompilerConfiguration(configuration: CompilerConfiguration, module: TestModule) {
        listOf(REFLEKT_PLUGIN, REFLEKT_DSL).forEach {
            val jar = findJar(it, testServices)
            configuration.addJvmClasspathRoot(jar)
        }
        configuration.addJvmClasspathRoot(getKotlinCompilerJar())
    }
}
