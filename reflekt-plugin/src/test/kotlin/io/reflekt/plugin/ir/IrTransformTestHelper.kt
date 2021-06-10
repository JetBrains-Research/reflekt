package io.reflekt.plugin.ir

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import io.reflekt.plugin.ReflektComponentRegistrar
import io.reflekt.plugin.util.Util
import io.reflekt.util.FileUtil
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.junit.jupiter.api.Assertions

object IrTransformTestHelper {
    private val commonTestFiles = FileUtil.getAllNestedFiles(Util.getResourcesRootPath(IrTransformTestHelper::class, "commonTestFiles"))
        .map { SourceFile.fromPath(it) }

    fun classFqNames(reflektCall: String): Set<String> = classOrObjectFqNames(reflektCall)

    fun objectFqNames(reflektCall: String): Set<String> = classOrObjectFqNames(reflektCall, classSuffix = "::class")

    private fun classOrObjectFqNames(reflektCall: String, classSuffix: String = ""): Set<String> {
        val mainFile = SourceFile.kotlin("Main.kt", """
package io.reflekt.test.ir

import io.reflekt.Reflekt

fun getResult() = $reflektCall.toList().map { it$classSuffix.qualifiedName!! }.toSet()
        """)
        return reflektCallResult(mainFile)
    }

    fun functionStrings(reflektCall: String, functionCallArguments: String): Set<String> {
        val mainFile = SourceFile.kotlin("Main.kt", """
package io.reflekt.test.ir

import io.reflekt.Reflekt

fun getResult() = $reflektCall.toList().also { functions ->
    functions.forEach {
        it($functionCallArguments)
    }
}.map { it.toString() }.toSet()
""")
        return reflektCallResult(mainFile)
    }

    private fun reflektCallResult(mainFile: SourceFile): Set<String> {
        val compilationResult = compile(commonTestFiles.plus(mainFile))
        Assertions.assertEquals(KotlinCompilation.ExitCode.OK, compilationResult.exitCode)
        val testResults = compilationResult.classLoader.loadClass("io.reflekt.test.ir.MainKt")
        val resultFun = testResults.getMethod("getResult")
        return resultFun.invoke(null) as Set<String>
    }

    private fun compile(
        sourceFiles: List<SourceFile>,
        plugin: ComponentRegistrar = ReflektComponentRegistrar(noConfiguration = true),
    ): KotlinCompilation.Result {
        return KotlinCompilation().apply {
            sources = sourceFiles
            jvmTarget = "11"
            compilerPlugins = listOf(plugin)
            inheritClassPath = true
            messageOutputStream
        }.compile()
    }

    private fun compile(
        sourceFile: SourceFile,
        plugin: ComponentRegistrar = ReflektComponentRegistrar(noConfiguration = true),
    ): KotlinCompilation.Result {
        return compile(listOf(sourceFile), plugin)
    }
}
