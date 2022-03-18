package org.jetbrains.reflekt.plugin.analysis.util

import org.jetbrains.reflekt.util.file.getAllNestedFiles
import org.jetbrains.reflekt.plugin.analysis.*
import org.jetbrains.reflekt.plugin.util.MavenLocalUtil.getReflektProjectJars
import org.jetbrains.reflekt.plugin.util.Util
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File

class FindSmartReflektInvokeArgumentsByExpressionPartTest {
    @Tag("analysis")
    @MethodSource("data")
    @ParameterizedTest(name = "test {index}")
    fun `findSmartReflektInvokeArgumentsByExpressionPart function test`(
        sources: Set<File>,
        expectedResult: String,
        directory: String) {
        val reflektClassPath = getReflektProjectJars()
        val analyzer = SmartReflektTestAnalyzer(AnalysisUtil.getBaseAnalyzer(classPath = reflektClassPath, sources = sources))
        Assertions.assertEquals(expectedResult, analyzer.analyze().toPrettyString(), "Incorrect invoke arguments for directory $directory")
    }

    companion object {
        @JvmStatic
        fun data(): List<Arguments> {
            // We change only the Main file in each test by using different configurations of the Reflekt invokes\uses
            val commonTestFiles = Util.getResourcesRootPath(AnalysisTest::class, "commonTestFiles").getAllNestedFiles().toSet()
            return getTestsDirectories(FindSmartReflektInvokeArgumentsByExpressionPartTest::class).map { directory ->
                val project = getProjectFilesInDirectory(directory)
                val supertypesToFilters = directory.findInDirectory("supertypesToFilters.txt").readTextNormalized()
                Arguments.of(commonTestFiles.union(project), supertypesToFilters, directory.name)
            }
        }
    }
}
