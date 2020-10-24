package io.reflekt.plugin.analysis

import io.reflekt.plugin.analysis.AnalysisUtil.getReflektAnalyzer
import io.reflekt.plugin.util.Util.getResourcesRootPath
import io.reflekt.util.FileUtil.getNestedDirectories
import org.gradle.internal.impldep.junit.framework.TestCase
import org.gradle.internal.impldep.org.junit.Test
import org.gradle.internal.impldep.org.junit.runner.RunWith
import org.gradle.internal.impldep.org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class AnalysisTest: TestCase() {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}")
        fun getTestData(): List<Array<Any>> {
            return getNestedDirectories(getResourcesRootPath(::AnalysisTest)).map { directory ->
                val classPath = directory.find("classPath")
                val project = directory.find("project")
                val invokes = parseInvokes(directory.find("invokes.json"))
                val uses = parseUses(directory.find("uses.json"))
                arrayOf(classPath, project, invokes, uses)
            }
        }

        private fun File.find(name: String): File {
            val baseErrorMessage = "in the directory $name was not found"
            return this.listFiles()?.first { it.name == name } ?: error("$name $baseErrorMessage")
        }

        private fun parseInvokes(json: File): ReflektInvokes {
            TODO("Not implemented yet")
        }

        private fun parseUses(json: File): ReflektUses {
            TODO("Not implemented yet")
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
