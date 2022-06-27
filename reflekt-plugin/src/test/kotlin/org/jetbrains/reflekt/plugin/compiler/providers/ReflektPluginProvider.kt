package org.jetbrains.reflekt.plugin.compiler.providers

import com.intellij.mock.MockProject
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoot
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.EnvironmentConfigurator
import org.jetbrains.kotlin.test.services.TestServices
import org.jetbrains.reflekt.plugin.ReflektComponentRegistrar
import org.jetbrains.reflekt.plugin.compiler.providers.ReflektClasspathProvider.REFLEKT_DSL
import org.jetbrains.reflekt.plugin.compiler.providers.ReflektClasspathProvider.REFLEKT_PLUGIN
import org.jetbrains.reflekt.plugin.compiler.providers.ReflektClasspathProvider.findJar
import org.jetbrains.reflekt.plugin.compiler.providers.ReflektClasspathProvider.getAbsolutePathsOfDefaultJars
import java.io.File

class ReflektPluginProvider(testServices: TestServices) : EnvironmentConfigurator(testServices) {
    override fun registerCompilerExtensions(project: Project, module: TestModule, configuration: CompilerConfiguration) {
        ReflektComponentRegistrar(true).registerProjectComponents(project as MockProject, configuration)
    }

    override fun configureCompilerConfiguration(configuration: CompilerConfiguration, module: TestModule) {
        listOf(REFLEKT_PLUGIN, REFLEKT_DSL).forEach {
            val jar = findJar(it, testServices)
            println(jar.absolutePath)
            configuration.addJvmClasspathRoot(jar)
        }
        getAbsolutePathsOfDefaultJars().forEach { configuration.addJvmClasspathRoot(it) }
    }
}
