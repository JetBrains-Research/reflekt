package io.reflekt.plugin.analysis

import io.reflekt.plugin.analysis.psi.KtDefaultVisitor
import io.reflekt.plugin.analysis.psi.function.*
import io.reflekt.plugin.analysis.util.FindSmartReflektInvokeArgumentsByExpressionPartTest
import io.reflekt.plugin.util.Util
import io.reflekt.util.FileUtil
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.junit.jupiter.params.provider.Arguments
import java.io.File

class ParametrizedTypeUtilTest {
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

//    @Tag("analysis")
//    @Test
//    fun `findSmartReflektInvokeArgumentsByExpressionPart function test`(sources: Set<File>, expectedResult: SubTypesToFiltersTest) {
//
//
//    }
}

//fun main() {
//    val reflektClassPath = AnalysisSetupTest.getReflektProjectJars()
//    val file = File("/Users/Elena.Lyulina/IdeaProjects/reflekt/reflekt-plugin/src/test/resources/io/reflekt/plugin/analysis/ParametrizedTypeTestFile.kt")
//    val baseAnalyzer = AnalysisUtil.getBaseAnalyzer(classPath = reflektClassPath, sources = setOf(file))
//    val visitor = MyVisitor(baseAnalyzer.binding)
//    baseAnalyzer.ktFiles.forEach {
//        it.acceptChildren(visitor)
//    }
//}
//
//class MyVisitor(val binding: BindingContext) : KtDefaultVisitor() {
//    override fun visitNamedFunction(function: KtNamedFunction) {
//        function.argumentTypes(binding).map { it.toParameterizedType() }.forEach { println(it) }
//        super.visitNamedFunction(function)
//    }
//}


