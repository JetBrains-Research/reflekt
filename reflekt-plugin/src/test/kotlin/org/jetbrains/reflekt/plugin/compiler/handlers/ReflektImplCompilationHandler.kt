package org.jetbrains.reflekt.plugin.compiler.handlers

import org.jetbrains.kotlin.test.services.TestServices
import org.jetbrains.kotlin.test.services.moduleStructure
import org.jetbrains.kotlin.test.services.standardLibrariesPathProvider
import org.jetbrains.reflekt.plugin.compiler.directives.ReflektGeneralCallDirectives.COMPILE_REFLEKT_IMPL
import org.jetbrains.reflekt.plugin.compiler.util.compileSources
import org.jetbrains.reflekt.plugin.compiler.util.createCompilerArguments
import org.jetbrains.reflekt.plugin.util.CodeGenTestPaths
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.File

class ReflektImplCompilationHandler(testServices: TestServices) : ReflektImplBaseHandler(testServices, COMPILE_REFLEKT_IMPL) {
    override fun processAfterAllModules(someAssertionWasFailed: Boolean) {
        assertDoesNotThrow {
            compileReflektImplFile()
        }
    }

    private fun compileReflektImplFile() {
        val testRoot = getTestRoot()
        val srcDir = File(testRoot, "src").apply { deleteIfExistsAndMkdirs() }
        getAndCopySources(srcDir)
        // We use internal makeIncrementally method for compilation, so we must have a folder for cashes
        val cacheDir = File(testRoot, "incremental-data").apply { deleteIfExistsAndMkdirs() }
        val outDir = File(testRoot, "out").apply { deleteIfExistsAndMkdirs() }
        val srcRoots = listOf(srcDir)
        val compilerArgs = createCompilerArguments(outDir, srcDir, compilerClasspath = testServices.getCompilerClasspath())
        compileSources(cacheDir, srcRoots, compilerArgs, "ReflektImpl")
    }

    private fun getAndCopySources(srcDir: File) {
        val sources = mutableListOf(getReflektImplFile())
        // TODO: Can we get compiled files and add into the classpath directly?
        val commonTestFiles =
            testServices.moduleStructure.modules.first().files.filter { CodeGenTestPaths.additionalSourcesCommonFilesFolder in it.originalFile.path }
                .map { it.originalFile }
        sources.addAll(commonTestFiles)
        for (source in sources) {
            source.copyRecursively(srcDir, overwrite = true)
        }
    }

    private fun TestServices.getCompilerClasspath() = listOf(
        this.standardLibrariesPathProvider.reflectJarForTests(),
        this.standardLibrariesPathProvider.runtimeJarForTestsWithJdk8()
    )

    private fun File.deleteIfExistsAndMkdirs() {
        if (this.exists()) {
            this.deleteRecursively()
        }
        mkdirs()
    }
}
