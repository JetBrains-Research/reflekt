package io.reflekt.plugin.ir

import io.reflekt.plugin.ReflektComponentRegistrar
import io.reflekt.plugin.util.Util
import io.reflekt.util.file.getAllNestedFiles

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.junit.jupiter.api.Assertions

/**
 * Represents source file with result method [resultMethod] that returns [T]
 * @property name
 * @property path
 * @property import
 * @property resultMethod
 * @property resultMethodBody
 */
data class ResultFile<T>(
    val name: String = "Main.kt",
    val path: String = "io.reflekt.test.ir",
    val import: String? = null,
    val resultMethod: String = "getResult",
    val resultMethodBody: String = "TODO()",
) {
    val classPath = "$path.${name.split('.').joinToString("") { it.replaceFirstChar(Char::titlecase) }}"
    val file = SourceFile.kotlin(name, """
package $path
${import?.let { "import $it" }}

fun $resultMethod() = $resultMethodBody
""")
}

/**
 * Compiles ResultFile with [commonTestFiles] and returns the call result of [ResultFile.resultMethod]
 */
object ResultCall {
    private val commonTestFiles = Util.getResourcesRootPath(ResultCall::class, "commonTestFiles").getAllNestedFiles()
        .map { SourceFile.fromPath(it) }

    fun <T> ResultFile<T>.call(useIR: Boolean = true): T {
        val compilationResult = KotlinCompilation().apply {
            sources = commonTestFiles.plus(file)
            jvmTarget = "11"
            compilerPlugins = listOf(ReflektComponentRegistrar(false))
            inheritClassPath = true
            messageOutputStream
            this.useIR = useIR
        }.compile()
        Assertions.assertEquals(KotlinCompilation.ExitCode.OK, compilationResult.exitCode)
        val testResults = compilationResult.classLoader.loadClass(classPath)
        val resultMethod = testResults.getMethod(resultMethod).invoke(null)
        return resultMethod as T
    }
}

/**
 * Signature of classes, objects, or functions passed to Reflekt calls
 * @property signature
 */
class Signature(vararg val signature: String) {
    /**
     * Fills signature with [filler] until [size] is reached
     *
     * @param size
     * @param filler
     * @return
     */
    fun fillToSize(size: Int, filler: String = ""): Array<out String> {
        if (signature.size >= size) {
            return signature
        }
        return arrayOf(*signature) + Array(size - signature.size) { filler }
    }
}

/**
 * Allows building functions, classes, or objects call result file with Reflekt or SmartReflekt
 * @property id
 * @property resolve
 */
enum class ReflektType(val id: String, val resolve: String) {
    REFLEKT("Reflekt", "toList()"),
    SMART_REFLEKT("SmartReflekt", "resolve()"),
    ;

    fun functionsInvokeCall(signature: Signature, functionsArguments: String): ResultFile<Set<String>> {
        val functionsCall = when (this) {
            REFLEKT -> "functions().withAnnotations<%s>(%s)".format(*signature.fillToSize(2))
            SMART_REFLEKT -> "functions<%s>().filter { %s }".format(*signature.fillToSize(2))
        }
        return resultFile("$id.$functionsCall.$resolve.also { f -> f.forEach { it($functionsArguments) } }.map { it.toString() }.toSet()")
    }

    fun objectsFqNamesCall(signature: Signature): ResultFile<Set<String>> {
        val objectsCall = when (this) {
            REFLEKT -> "objects().withSupertype<%s>(%s)".format(*signature.fillToSize(2))
            SMART_REFLEKT -> "objects<%s>().filter { %s }".format(*signature.fillToSize(2))
        }
        return resultFile("$id.$objectsCall.$resolve.map { it::class.qualifiedName!! }.toSet()")
    }

    fun classesFqNamesCall(signature: Signature): ResultFile<Set<String>> {
        val classesCall = when (this) {
            REFLEKT -> "classes().withSupertype<%s>(%s)".format(*signature.fillToSize(2))
            SMART_REFLEKT -> "classes<%s>().filter { %s }".format(*signature.fillToSize(2))
        }
        return resultFile("$id.$classesCall.$resolve.map { it.qualifiedName!! }.toSet()")
    }

    private fun resultFile(resultMethodBody: String) = ResultFile<Set<String>>(import = "io.reflekt.$id", resultMethodBody = resultMethodBody)
}
