package io.reflekt.plugin.code.generation

import io.reflekt.plugin.analysis.*
import io.reflekt.plugin.generation.code.generator.ReflektImplGenerator
import io.reflekt.plugin.util.Util
import io.reflekt.util.FileUtil
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
            return getTestsDirectories(CodeGenerationTest::class).filter { !it.name.endsWith("functions3_test") }.map { directory ->
                val project = getProjectFilesInDirectory(directory)
                val generatedCode = directory.findInDirectory("generatedCode.kt").readText().trim()
                Arguments.of(commonTestFiles.union(project), generatedCode, directory.name)
            }
        }
    }

    @Tag("codegen")
    @MethodSource("data")
    @ParameterizedTest(name = "test {index}")
    fun `code generation test`(sources: Set<File>, expectedCode: String, directory: String) {
        val reflektClassPath = AnalysisSetupTest.getReflektProjectJars()
        val analyzer = AnalysisUtil.getReflektAnalyzer(classPath = reflektClassPath, sources = sources)
        val uses = analyzer.uses(analyzer.invokes())
        val actualCode = ReflektImplGenerator(uses).generate().trim()
        Assertions.assertEquals(expectedCode, actualCode, "Incorrect generated code for directory $directory")
    }

}
