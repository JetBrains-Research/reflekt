package org.jetbrains.reflekt.plugin.compiler.providers

import org.jetbrains.kotlin.test.services.TestServices
import org.jetbrains.kotlin.test.services.assertions
import java.io.File
import java.io.FilenameFilter

object ReflektClasspathProvider {
    const val REFLEKT_PLUGIN = "reflekt-plugin"
    const val REFLEKT_CORE = "reflekt-dsl"

    fun findJar(moduleName: String, testServices: TestServices): File {
        val libDir = File(jarDir(moduleName))
        testServices.assertions.assertTrue(libDir.exists() && libDir.isDirectory, failMessage(moduleName))
        return libDir.listFiles(jarFilter(moduleName))?.firstOrNull() ?: testServices.assertions.fail(failMessage(moduleName))
    }

    private fun jarDir(moduleName: String) = "$moduleName/build/libs/"

    private fun jarFilter(moduleName: String) = FilenameFilter { _, name -> name.startsWith(moduleName) && name.endsWith(".jar") && !name.contains("-sources") }

    private fun failMessage(moduleName: String) = { "Jar with Reflekt for the module: $moduleName does not exist. Please run :$moduleName:jar" }
}
