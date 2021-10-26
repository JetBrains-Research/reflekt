package org.jetbrains.reflekt.plugin.ic

import org.jetbrains.kotlin.cli.common.arguments.parseCommandLineArguments
import org.jetbrains.reflekt.plugin.analysis.getTestsDirectories
import org.jetbrains.reflekt.plugin.ic.modification.Modification
import org.jetbrains.reflekt.plugin.ic.modification.applyModifications
import org.jetbrains.reflekt.plugin.util.Util
import org.jetbrains.reflekt.plugin.util.Util.clear
import org.jetbrains.reflekt.plugin.util.Util.getTempPath
import org.jetbrains.reflekt.util.FileUtil
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File

class IncrementalCompilationTest {
    // File with compiler arguments (see K2JVMCompilerArguments)
    // If we would like to add additional arguments in tests we can use this file
    private val argumentsFileName = "args.txt"

    // The name of file with the main function
    private val mainFileName = "Main"
    private val outFolderName = "out"

    companion object {
        @JvmStatic
        fun data(): List<Arguments> {
            return getTestsDirectories(IncrementalCompilationTest::class).map { directory ->
                // TODO: get modifications for each directory (maybe deserialize it?)
                Arguments.of(directory, emptyList<Modification>(), null)
            }
        }
    }

    @Tag("ic")
    @MethodSource("data")
    @ParameterizedTest(name = "test {index}")
    fun incrementalCompilationBaseTest(sourcesPath: File, modifications: List<Modification>, expectedResult: String?) {
        val testRoot = initTestRoot()
        val srcDir = File(testRoot, "src").apply { mkdirs() }
        val cacheDir = File(testRoot, "incremental-data").apply { mkdirs() }
        val outDir = File(testRoot, outFolderName).apply { mkdirs() }
        val srcRoots = listOf(srcDir)

        sourcesPath.copyRecursively(srcDir, overwrite = true)
        val testDataPath = File(Util.getResourcesRootPath(IncrementalCompilationTest::class))
        val pathToDownloadKotlinSources = File(testDataPath.parent, "kotlinSources").apply { mkdirs() }
        val compilerArgs = createCompilerArguments(outDir, srcDir, pathToDownloadKotlinSources).apply {
            parseCommandLineArguments(parseAdditionalCompilerArgs(srcDir, argumentsFileName), this)
        }
        compileSources(cacheDir, srcRoots, compilerArgs, "Initial")
        // If expectedResult was not passed then the initial result should be the same
        // with the result after sources modification
        val realExpectedResult = expectedResult ?: runCompiledCode(outDir)

        modifications.applyModifications()
        compileSources(cacheDir, srcRoots, compilerArgs, "Modified")
        val actualResult = runCompiledCode(outDir)
        Assertions.assertEquals(realExpectedResult, actualResult, "Result after IC is incorrect")

        // Compare the initial result and result without IC
        cacheDir.clear()
        compileSources(cacheDir, srcRoots, compilerArgs, "Without IC")
        val actualResultWithoutIC = runCompiledCode(outDir)
        Assertions.assertEquals(realExpectedResult, actualResultWithoutIC, "The initial result and result after IC are different!")

        testRoot.deleteRecursively()
    }

    private fun runCompiledCode(outDir: File) = Util.runProcessBuilder(Util.Command(listOf("java", getMainClass(outDir)), directory = outDir.absolutePath))

    // Find [mainFileName]Kt.class file in [outDir] and make the following transformations:
    //  - сut <class> extension
    //  - get the relative path with [outDir]
    //  - replace all "/" into "."
    private fun getMainClass(outDir: File): String {
        val allFiles = FileUtil.getAllNestedFiles(outDir.absolutePath)
        val mainClass = allFiles.find { it.name == "${mainFileName}Kt.class" }?.absolutePath?.removeSuffix(".class")
            ?: error("The output directory doe not contains ${mainFileName}Kt.class file")
        return mainClass.substring(mainClass.indexOf("$outFolderName/") + outFolderName.length + 1).replace("/", ".")
    }

    // If we had failed tests the previous results were not deleted and it can throw some compiler errors
    private fun initTestRoot(): File {
        val testRoot = File(getTempPath(), IncrementalCompilationTest::class.java.simpleName)
        if (testRoot.exists()) {
            testRoot.deleteRecursively()
        }
        testRoot.apply { mkdirs() }
        return testRoot
    }
}
