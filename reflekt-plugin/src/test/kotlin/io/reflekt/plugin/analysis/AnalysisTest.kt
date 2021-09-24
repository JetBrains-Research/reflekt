package io.reflekt.plugin.analysis

import io.reflekt.plugin.analysis.AnalysisUtil.getReflektAnalyzer
import io.reflekt.plugin.analysis.models.ReflektInvokes
import io.reflekt.plugin.util.MavenLocalUtil.getReflektProjectJars
import io.reflekt.plugin.util.Util.getResourcesRootPath
import io.reflekt.plugin.util.Util.parseJson
import io.reflekt.util.FileUtil.getAllNestedFiles
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File

class AnalysisTest {

    companion object {
        @JvmStatic
        fun data(): List<Arguments> {
            // We change only the Main file in each test by using different configurations of the Reflekt invokes\uses
            val commonTestFiles = getAllNestedFiles(getResourcesRootPath(AnalysisTest::class, "commonTestFiles")).toSet()
            return getTestsDirectories(AnalysisTest::class).map { directory ->
                val project = getProjectFilesInDirectory(directory)
                val invokes = directory.findInDirectory("invokes.txt").readText().trim()
                val uses = directory.findInDirectory("uses.txt").readText().trim()
                Arguments.of(commonTestFiles.union(project), invokes, uses, directory.name)
            }
        }
    }

    @Tag("analysis")
    @MethodSource("data")
    @ParameterizedTest(name = "test {index}")
    fun `project analyzer test`(sources: Set<File>, expectedInvokes: String, expectedUses: String, directory: String) {
        val reflektClassPath = getReflektProjectJars()
        val analyzer = getReflektAnalyzer(classPath = reflektClassPath, sources = sources)
        val actualInvokes = analyzer.invokes()
        Assertions.assertEquals(expectedInvokes, actualInvokes.toPrettyString(), "Incorrect invokes for directory $directory")
        val actualUses = analyzer.uses(actualInvokes)
        Assertions.assertEquals(expectedUses, actualUses.toPrettyString(), "Incorrect uses for directory $directory")
    }
}


