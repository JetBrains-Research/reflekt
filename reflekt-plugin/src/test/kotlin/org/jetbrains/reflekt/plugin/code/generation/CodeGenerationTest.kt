package org.jetbrains.reflekt.plugin.code.generation

import org.jetbrains.reflekt.plugin.analysis.*
import org.jetbrains.reflekt.plugin.generation.code.generator.ReflektImplGenerator
import org.jetbrains.reflekt.plugin.util.Util
import org.jetbrains.reflekt.util.file.getAllNestedFiles
import org.jetbrains.reflekt.plugin.util.MavenLocalUtil.getReflektProjectJars
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File

class CodeGenerationTest {
    @Tag("codegen")
    @MethodSource("data")
    @ParameterizedTest(name = "test {index}")
    fun `code generation test`(
        sources: Set<File>,
        expectedCode: String,
        directory: String) {
        val reflektClassPath = getReflektProjectJars()
        val analyzer = AnalysisUtil.getReflektAnalyzer(classPath = reflektClassPath, sources = sources)
        val uses = analyzer.uses(analyzer.invokes())
        val actualCode = ReflektImplGenerator(uses).generate().trim()
        Assertions.assertEquals(expectedCode, actualCode, "Incorrect generated code for directory $directory")
    }
    companion object {
        @JvmStatic
        fun data(): List<Arguments> {
            // We change only the Main file in each test by using different configurations of the Reflekt invokes\uses
            val commonTestFiles = Util.getResourcesRootPath(CodeGenerationTest::class, "commonTestFiles").getAllNestedFiles().toSet()
            return getTestsDirectories(CodeGenerationTest::class).map { directory ->
                val project = getProjectFilesInDirectory(directory)
                // We use txt format instead of kt files since each of generatedCode file has the same package name
                // and Idea highlights it as en error
                val generatedCode = directory.findInDirectory("generatedCode.txt").readTextNormalized()
                Arguments.of(commonTestFiles.union(project), generatedCode, directory.name)
            }
        }
    }
}
