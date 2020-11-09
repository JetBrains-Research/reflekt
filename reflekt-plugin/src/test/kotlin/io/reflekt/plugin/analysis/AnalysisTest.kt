package io.reflekt.plugin.analysis

import io.reflekt.plugin.analysis.AnalysisUtil.getReflektAnalyzer
import io.reflekt.plugin.util.Util.getResourcesRootPath
import io.reflekt.plugin.util.Util.parseJson
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
            // We change only the Main file in each test by using different configurations of the Reflekt invokes\uses
            val commonTestFiles = getAllNestedFiles(getResourcesRootPath(::AnalysisTest, "commonTestFiles")).toSet()
            return getNestedDirectories(getResourcesRootPath(::AnalysisTest)).sorted().filter { "test" in it.name }.map { directory ->
                val project = getAllNestedFiles(directory.findInDirectory("project", true).absolutePath, ignoredDirectories = setOf(".idea")).toSet()
                val invokes = parseInvokes(directory.findInDirectory("invokes.json"))
                val uses = parseUses(directory.findInDirectory("uses.json"))
                Arguments.of(commonTestFiles.union(project), invokes, uses)
            }
        }

        private fun File.findInDirectory(name: String, toCreateIfDoesNotExist: Boolean = false): File {
            if (!this.isDirectory) {
                error("${this.absolutePath} is not a directory")
            }
            val baseErrorMessage = "in the directory ${this.name} was not found"
            return this.listFiles()?.firstOrNull { it.name == name } ?: run {
                if (toCreateIfDoesNotExist) {
                    val res = File("${this.absolutePath}/$name")
                    res.mkdirs()
                    res
                } else {
                    error("$name $baseErrorMessage")
                }
            }
        }

        private fun parseInvokes(json: File): ReflektInvokes = parseJson(json)

        // TODO: can we do it better?
        private fun parseUses(json: File): ReflektUses = parseJson<ReflektUsesTest>(json).toReflektUses()
    }

    /*
     * We have lists in ClassOrObjectUses. It means if two lists have the same elements,
     * but the elements have the different permutations,
     * the lists will be different. But in our case it is not true
     */
    private fun ClassOrObjectUses.equalTo(expected: ClassOrObjectUses): Boolean {
        if (this.keys != expected.keys) {
            return false
        }
        this.forEach { (annotations, v) ->
            val expectedAnnotations = expected[annotations] ?: return false
            if (!equal(v, expectedAnnotations)) {
                return false
            }
        }
        return true
    }

    private fun <T> equal(first: MutableMap<Set<String>, MutableList<T>>,
                      second: MutableMap<Set<String>, MutableList<T>>): Boolean {
        if (first.keys != second.keys) {
            return false
        }
        first.forEach { (set, lst) ->
            val expectedLst = second[set] ?: return false
            if (lst.size != expectedLst.size || !lst.containsAll(expectedLst)) {
                return false
            }
        }
        return true
    }

    private fun ReflektUses.equalTo(expected: ReflektUses): Boolean {
        return listOf(this.objects.equalTo(expected.objects), this.classes.equalTo(expected.classes), equal(this.functions, expected.functions)).all { it }
    }

    @Tag("analysis")
    @MethodSource("data")
    @ParameterizedTest(name = "test {index}")
    fun `project analyzer test`(sources: Set<File>, expectedInvokes: ReflektInvokes, expectedUses: ReflektUses) {
        val reflektClassPath = AnalysisSetupTest.getReflektJars()
        val analyzer = getReflektAnalyzer(classPath = reflektClassPath, sources = sources)
        val actualInvokes = analyzer.invokes()
        Assertions.assertEquals(expectedInvokes, actualInvokes)
        val actualUses = analyzer.uses(actualInvokes)
        Assertions.assertTrue(actualUses.equalTo(expectedUses))
    }
}
