package io.reflekt.plugin.analysis

import io.reflekt.plugin.analysis.AnalysisUtil.getReflektAnalyzer
import io.reflekt.plugin.util.Util.getResourcesRootPath
import io.reflekt.util.FileUtil.getAllNestedFiles
import io.reflekt.util.FileUtil.getNestedDirectories
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
            return getNestedDirectories(getResourcesRootPath(::AnalysisTest)).map { directory ->
                val classPath = getAllNestedFiles(directory.findInDirectory("classPath").absolutePath).toSet()
                val project = getAllNestedFiles(directory.findInDirectory("project").absolutePath).toSet()
                val invokes = parseInvokes(directory.findInDirectory("invokes.json"))
                val uses = parseUses(directory.findInDirectory("uses.json"))
                Arguments.of(classPath, project, invokes, uses)
            }
        }

        private fun File.findInDirectory(name: String): File {
            if (!this.isDirectory) {
                error("${this.absolutePath} is not a directory")
            }
            val baseErrorMessage = "in the directory ${this.name} was not found"
            return this.listFiles()?.first { it.name == name } ?: error("$name $baseErrorMessage")
        }

        private fun parseInvokes(json: File): ReflektInvokes {
            // TODO "Not implemented yet"
            return ReflektInvokes()
        }

        private fun parseUses(json: File): ReflektUses {
            // TODO "Not implemented yet"
            return ReflektUses()
        }
    }

    @Tag("analysis")
    @MethodSource("data")
    @ParameterizedTest(name = "test number {index}")
    fun `project analyzer test`(classPath: Set<File>, sources: Set<File>, expectedInvokes: ReflektInvokes, expectedUses: ReflektUses) {
        val analyzer = getReflektAnalyzer(classPath, sources)
        val actualInvokes = analyzer.invokes()
        Assertions.assertEquals(actualInvokes, expectedInvokes)
        val actualUses = analyzer.uses(actualInvokes)
        Assertions.assertEquals(actualUses, expectedUses)
    }
}
