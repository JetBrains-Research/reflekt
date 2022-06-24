package org.jetbrains.reflekt.plugin.compiler.providers

import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoot
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.EnvironmentConfigurator
import org.jetbrains.kotlin.test.services.TestServices
import org.jetbrains.kotlin.test.services.assertions
import java.io.File
import java.io.FilenameFilter

class ReflektPluginProvider(testServices: TestServices) : EnvironmentConfigurator(testServices) {
    companion object {
        private const val REFLEKT_JAR_DIR = "reflekt-plugin/build/libs/"
        private val REFLEKT_JAR_FILTER = FilenameFilter { _, name -> name.startsWith("reflekt-plugi") && name.endsWith(".jar") }
    }

    override fun configureCompilerConfiguration(configuration: CompilerConfiguration, module: TestModule) {
        val libDir = File(REFLEKT_JAR_DIR)
        testServices.assertions.assertTrue(libDir.exists() && libDir.isDirectory, failMessage)
        val jar = libDir.listFiles(REFLEKT_JAR_FILTER)?.firstOrNull() ?: testServices.assertions.fail(failMessage)
        configuration.addJvmClasspathRoot(jar)
    }

    private val failMessage = { "Jar with Reflekt does not exist. Please run :reflekt-plugin:jar" }
}
