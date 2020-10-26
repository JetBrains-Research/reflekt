package io.reflekt.plugin.analysis

import io.reflekt.plugin.analysis.AnalysisUtil.getReflektAnalyzer
import io.reflekt.plugin.util.Util.getResourcesRootPath
import io.reflekt.util.FileUtil.getAllNestedFiles
import io.reflekt.util.FileUtil.getNestedDirectories
import org.gradle.internal.impldep.junit.framework.TestCase
import org.gradle.internal.impldep.org.junit.Test
import org.gradle.internal.impldep.org.junit.runner.RunWith
import org.gradle.internal.impldep.org.junit.runners.Parameterized
import java.io.File


@RunWith(Parameterized::class)
class AnalysisTest : TestCase() {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}")
        fun getTestData(): List<Array<Any>> {
            return getNestedDirectories(getResourcesRootPath(::AnalysisTest)).map { directory ->
                val classPath = getAllNestedFiles(directory.findInDirectory("classPath").absolutePath)
                val project = getAllNestedFiles(directory.findInDirectory("project").absolutePath)
                val invokes = parseInvokes(directory.findInDirectory("invokes.json"))
                val uses = parseUses(directory.findInDirectory("uses.json"))
                arrayOf(classPath, project, invokes, uses)
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

    @JvmField
    @Parameterized.Parameter(0)
    var classPath: Set<File> = emptySet()

    @JvmField
    @Parameterized.Parameter(1)
    var sources: Set<File> = emptySet()

    @JvmField
    @Parameterized.Parameter(2)
    var expectedInvokes: ReflektInvokes? = null

    @JvmField
    @Parameterized.Parameter(3)
    var expectedUses: ReflektUses? = null

    @Test
    fun `project analyzer test`() {
        val analyzer = getReflektAnalyzer(classPath, sources)
        val actualInvokes = analyzer.invokes()
        assertEquals(actualInvokes, expectedInvokes)
        val actualUses = analyzer.uses(actualInvokes)
        assertEquals(actualUses, expectedUses)
    }
}
