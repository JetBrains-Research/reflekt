package org.jetbrains.reflekt.plugin.compiler.util

import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.incremental.makeJvmIncrementally
import org.jetbrains.reflekt.plugin.utils.Util
import java.io.File

internal fun createCompilerArguments(destinationDir: File, testDir: File, compilerClasspath: List<File> = emptyList()): K2JVMCompilerArguments =
    K2JVMCompilerArguments().apply {
        moduleName = testDir.name
        destination = destinationDir.path
        classpath = compilerClasspath.joinToString(File.pathSeparator) { it.absolutePath }
    }

// This function does not take into account java sources
internal fun compile(cacheDir: File, sourceRoots: Iterable<File>, args: K2JVMCompilerArguments): TestCompilationResult {
    val reporter = TestICReporter()
    val messageCollector = TestMessageCollector()
    makeJvmIncrementally(cacheDir, sourceRoots, args, reporter = reporter, messageCollector = messageCollector)
    return TestCompilationResult(reporter, messageCollector)
}

internal fun compileSources(cacheDir: File, sources: List<File>, compilerArgs: K2JVMCompilerArguments, errorMessagePrefix: String) {
    val (_, errors) = compile(cacheDir, sources, compilerArgs)
    check(errors.isEmpty()) { "$errorMessagePrefix build failed:\n${errors.joinToString("\n")}" }
}
