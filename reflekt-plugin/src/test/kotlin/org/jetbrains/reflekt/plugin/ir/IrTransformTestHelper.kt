package org.jetbrains.reflekt.plugin.ir

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.reflekt.util.file.getAllNestedFiles
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.reflekt.plugin.ReflektComponentRegistrar
import org.jetbrains.reflekt.plugin.util.Util
import org.junit.jupiter.api.Assertions

object IrTransformTestHelper {
    private val commonTestFiles = Util.getResourcesRootPath(IrTransformTestHelper::class, "commonTestFiles").getAllNestedFiles()
        .map { SourceFile.fromPath(it) }

    fun classFqNames(reflektCall: String): Set<String> = classOrObjectFqNames(reflektCall)

    fun objectFqNames(reflektCall: String): Set<String> = classOrObjectFqNames(reflektCall, classSuffix = "::class")

    private fun classOrObjectFqNames(reflektCall: String, classSuffix: String = ""): Set<String> {
        val mainFile = SourceFile.kotlin(
            "Main.kt", """
package org.jetbrains.reflekt.test.ir

import org.jetbrains.reflekt.Reflekt

fun getResult() = $reflektCall.toList().map { it$classSuffix.qualifiedName!! }.toSet()
        """
        )
        return reflektCallResult(mainFile)
    }

    fun functionStrings(reflektCall: String, functionCallArguments: String): Set<String> {
        val mainFile = SourceFile.kotlin(
            "Main.kt", """
package org.jetbrains.reflekt.test.ir

import org.jetbrains.reflekt.Reflekt

fun getResult() = $reflektCall.toList().also { functions ->
    functions.forEach {
        it($functionCallArguments)
    }
}.map { it.toString() }.toSet()
"""
        )
        return reflektCallResult(mainFile)
    }

    private fun reflektCallResult(mainFile: SourceFile): Set<String> {
        val compilationResult = compile(commonTestFiles.plus(mainFile))
        Assertions.assertEquals(KotlinCompilation.ExitCode.OK, compilationResult.exitCode)
        val testResults = compilationResult.classLoader.loadClass("org.jetbrains.reflekt.test.ir.MainKt")
        val resultFun = testResults.getMethod("getResult")
        return resultFun.invoke(null) as Set<String>
    }

    private fun compile(
        sourceFiles: List<SourceFile>,
        plugin: ComponentRegistrar = ReflektComponentRegistrar(isTestConfiguration = true),
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
        plugin: ComponentRegistrar = ReflektComponentRegistrar(isTestConfiguration = true),
    ): KotlinCompilation.Result {
        return compile(listOf(sourceFile), plugin)
    }
}
