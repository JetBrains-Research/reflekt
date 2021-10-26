package org.jetbrains.reflekt.plugin.code.generation

import org.jetbrains.reflekt.plugin.analysis.*
import org.jetbrains.reflekt.plugin.generation.code.generator.ReflektImplGenerator
import org.jetbrains.reflekt.plugin.util.MavenLocalUtil.getReflektProjectJars
import org.jetbrains.reflekt.plugin.util.Util
import org.jetbrains.reflekt.util.FileUtil
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File

class CodeGenerationTest {

    companion object {
        @JvmStatic
        fun data(): List<Arguments> {
            // We change only the Main file in each test by using different configurations of the Reflekt invokes\uses
            val commonTestFiles = FileUtil.getAllNestedFiles(Util.getResourcesRootPath(CodeGenerationTest::class, "commonTestFiles")).toSet()
            return getTestsDirectories(CodeGenerationTest::class).filter { "classes1_test" in it.absolutePath }.map { directory ->
                val project = getProjectFilesInDirectory(directory)
                // We use txt format instead of kt files since each of generatedCode file has the same package name
                // and Idea highlights it as en error
                val generatedCode = directory.findInDirectory("generatedCode.txt").readText().trim()
                Arguments.of(commonTestFiles.union(project), generatedCode, directory.name)
            }
        }
    }

    @Tag("codegen")
    @MethodSource("data")
    @ParameterizedTest(name = "test {index}")
    fun `code generation test`(sources: Set<File>, expectedCode: String, directory: String) {
        val reflektClassPath = getReflektProjectJars()
        val analyzer = AnalysisUtil.getReflektAnalyzer(classPath = reflektClassPath, sources = sources)
        val invokes = analyzer.invokes()
        val uses = analyzer.uses(invokes)
        val actualCode = ReflektImplGenerator(uses).generate().trim()
        Assertions.assertEquals(expectedCode, actualCode, "Incorrect generated code for directory $directory")
    }

}