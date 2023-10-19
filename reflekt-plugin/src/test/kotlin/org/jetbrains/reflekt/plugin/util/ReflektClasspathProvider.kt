package org.jetbrains.reflekt.plugin.util

import org.jetbrains.kotlin.test.services.TestServices
import org.jetbrains.kotlin.test.services.assertions
import java.io.File
import java.io.FilenameFilter

object ReflektClasspathProvider {
    data class DirectoryAndModule(val jarDirectory: String, val module: String)

    val REFLEKT_PLUGIN = DirectoryAndModule("reflekt-plugin/build/libs/", "reflekt-plugin")
    val REFLEKT_DSL = DirectoryAndModule("using-embedded-kotlin/reflekt-dsl/build/libs/", "reflekt-dsl")

    fun findJar(directoryAndModule: DirectoryAndModule, testServices: TestServices? = null): File {
        val libDir = File(directoryAndModule.jarDirectory)
        testServices?.assertions?.assertTrue(libDir.exists() && libDir.isDirectory) { failMessage(directoryAndModule.module) }
        return libDir.listFiles(jarFilter(directoryAndModule.module))?.firstOrNull()
            ?: testServices?.assertions?.fail { failMessage(directoryAndModule.module) }
            ?: error(failMessage(directoryAndModule.module))
    }

    private fun jarFilter(moduleName: String) = FilenameFilter { _, name ->
        name.startsWith(moduleName) && name.endsWith(".jar") && !name.contains("-sources")
    }

    private fun failMessage(moduleName: String) = "Jar with Reflekt for the module: $moduleName does not exist. Please run :$moduleName:jar"
}

object CodeGenTestPaths {
    private const val ROOT_COMPILER_TEST_FOLDER = "reflekt-plugin/src/test/resources/org/jetbrains/reflekt/plugin/compiler"
    private val additionalSourcesFolder = "$ROOT_COMPILER_TEST_FOLDER/additional-sources"
    val additionalSourcesCommonFilesFolder = "$additionalSourcesFolder/common-files"
    val additionalSourcesHelpersFolder = "$additionalSourcesFolder/helpers"
    val codeGenResourcesFolder = "$ROOT_COMPILER_TEST_FOLDER/code-gen"
}
