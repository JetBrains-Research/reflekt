package org.jetbrains.reflekt.plugin.compiler.providers

import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoot
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.*
import java.io.File
import java.io.FilenameFilter

class ReflektPluginProvider(testServices: TestServices) : EnvironmentConfigurator(testServices) {
    companion object {
        private const val REFLEKT_PLUGIN = "reflekt-plugin"
        private const val REFLEKT_CORE = "reflekt-dsl"
    }

    override fun configureCompilerConfiguration(configuration: CompilerConfiguration, module: TestModule) {
        listOf(REFLEKT_PLUGIN, REFLEKT_CORE).forEach {
            configuration.addJvmClasspathRoot(findJar(it))
        }
    }

    private fun findJar(moduleName: String): File {
        val libDir = File(jarDir(moduleName))
        testServices.assertions.assertTrue(libDir.exists() && libDir.isDirectory, failMessage(moduleName))
        return libDir.listFiles(jarFilter(moduleName))?.firstOrNull() ?: testServices.assertions.fail(failMessage(moduleName))
    }

    // TODO: can we do it better?
    private fun pluginRoot() = File(System.getProperty("user.dir")).parent

    private fun jarDir(moduleName: String) = "${pluginRoot()}/$moduleName/build/libs/"

    private fun jarFilter(moduleName: String) = FilenameFilter { _, name -> name.startsWith(moduleName) && name.endsWith(".jar") }

    private fun failMessage(moduleName: String) = { "Jar with Reflekt for the module: $moduleName does not exist. Please run :$moduleName:jar" }
}
