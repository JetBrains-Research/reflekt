package org.jetbrains.reflekt.plugin.analysis

import java.io.File
import org.jetbrains.reflekt.plugin.analysis.AnalysisUtil.getReflektAnalyzer
import org.jetbrains.reflekt.plugin.util.MavenLocalUtil.getReflektProjectJars
import org.jetbrains.reflekt.plugin.util.Util.getResourcesRootPath
import org.jetbrains.reflekt.util.file.getAllNestedFiles
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class AnalysisTest {
    @Tag("analysis")
    @MethodSource("data")
    @ParameterizedTest(name = "[{index}] {3}")
    fun `project analyzer test`(
        sources: Set<File>,
        expectedInvokes: String,
        expectedUses: String,
        directory: String
    ) {
        val reflektClassPath = getReflektProjectJars()
        val analyzer = getReflektAnalyzer(classPath = reflektClassPath, sources = sources)
        val actualInvokes = analyzer.invokes()
        Assertions.assertEquals(expectedInvokes, actualInvokes.toPrettyString(), "Incorrect invokes for directory $directory")
        val actualUses = analyzer.uses(actualInvokes)
        Assertions.assertEquals(expectedUses, actualUses.toPrettyString(), "Incorrect uses for directory $directory")
    }

    companion object {
        @JvmStatic
        fun data(): List<Arguments> {
            // We change only the Main file in each test by using different configurations of the Reflekt invokes\uses
            val commonTestFiles = getResourcesRootPath(AnalysisTest::class, "commonTestFiles").getAllNestedFiles().toSet()
            return getTestsDirectories(AnalysisTest::class).map { directory ->
                val project = getProjectFilesInDirectory(directory)
                val invokes = directory.findInDirectory("invokes.txt").readTextNormalized()
                val uses = directory.findInDirectory("uses.txt").readTextNormalized()
                Arguments.of(commonTestFiles.union(project), invokes, uses, directory.name)
            }
        }
    }
}
