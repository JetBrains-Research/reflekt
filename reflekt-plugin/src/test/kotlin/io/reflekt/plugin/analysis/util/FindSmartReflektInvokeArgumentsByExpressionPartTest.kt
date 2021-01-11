package io.reflekt.plugin.analysis.util

import io.reflekt.plugin.analysis.*
import io.reflekt.plugin.analysis.models.SubTypesToFilters
import io.reflekt.plugin.util.Util
import io.reflekt.util.FileUtil
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File

class FindSmartReflektInvokeArgumentsByExpressionPartTest {

    companion object {
        @JvmStatic
        fun data(): List<Arguments> {
            // We change only the Main file in each test by using different configurations of the Reflekt invokes\uses
            val commonTestFiles = FileUtil.getAllNestedFiles(Util.getResourcesRootPath(AnalysisTest::class, "commonTestFiles")).toSet()
            return getTestsDirectories(FindSmartReflektInvokeArgumentsByExpressionPartTest::class).map { directory ->
                val project = getProjectFilesInDirectory(directory)
                val subTypesToFilters = parseSubTypesToFilters(directory.findInDirectory("subTypesToFilters.json"))
                Arguments.of(commonTestFiles.union(project), subTypesToFilters)
            }
        }

        private fun parseSubTypesToFilters(json: File): SubTypesToFiltersTest = Util.parseJson(json)
    }

    @Tag("analysis")
    @MethodSource("data")
    @ParameterizedTest(name = "test {index}")
    fun `findSmartReflektInvokeArgumentsByExpressionPart function test`(sources: Set<File>, expectedResult: SubTypesToFiltersTest) {
        val reflektClassPath = AnalysisSetupTest.getReflektProjectJars()
        val analyzer = SmartReflektTestAnalyzer(AnalysisUtil.getBaseAnalyzer(classPath = reflektClassPath, sources = sources))
        Assertions.assertEquals(expectedResult, analyzer.analyze().toSubTypesToFiltersTest())
    }
}
