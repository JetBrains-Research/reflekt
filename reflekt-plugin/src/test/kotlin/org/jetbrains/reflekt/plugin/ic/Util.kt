package org.jetbrains.reflekt.plugin.ic

import org.jetbrains.reflekt.plugin.util.MavenLocalUtil.getReflektProjectJars
import org.jetbrains.reflekt.plugin.util.MavenLocalUtil.getStdLibJar
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.incremental.makeIncrementally
import org.jetbrains.reflekt.plugin.ic.modification.Modification
import java.io.File

internal fun createCompilerArguments(destinationDir: File, testDir: File, compilerClassPath: List<String>): K2JVMCompilerArguments {
    return K2JVMCompilerArguments().apply {
        moduleName = testDir.name
        destination = destinationDir.path
        pluginClasspaths = compilerClassPath.filter { "reflekt" in it }.toTypedArray()
        pluginOptions = arrayOf("plugin:org.jetbrains.reflekt:enabled=true")
        classpath = compilerClassPath.joinToString(File.pathSeparator)
        jvmTarget = "11"
        useIR = true
    }
}

internal fun getCompilerClasspath(pathToDownloadKotlinSources: File): List<String> {
    val reflektJars = getReflektProjectJars().map { it.canonicalPath }
    val compilerClasspath = reflektJars.toMutableList()
    compilerClasspath.add(getStdLibJar(pathToDownloadKotlinSources).canonicalPath)
    return compilerClasspath
}

// This function does not take into account java sources
internal fun compile(cacheDir: File, sourceRoots: Iterable<File>, args: K2JVMCompilerArguments): TestCompilationResult {
    val reporter = TestICReporter()
    val messageCollector = TestMessageCollector()
    makeIncrementally(cacheDir, sourceRoots, args, reporter = reporter, messageCollector = messageCollector)
    return TestCompilationResult(reporter, messageCollector)
}

internal fun parseAdditionalCompilerArgs(testDir: File, argumentsFileName: String): List<String> {
    return File(testDir, argumentsFileName)
        .takeIf { it.exists() }
        ?.readText()
        ?.split(" ", "\n")
        ?.filter { it.isNotBlank() }
        ?: emptyList()
}

internal fun compileSources(cacheDir: File, sources: List<File>, compilerArgs: K2JVMCompilerArguments, errorMessagePrefix: String) {
    val (_, errors) = compile(cacheDir, sources, compilerArgs)
    check(errors.isEmpty()) { "$errorMessagePrefix build failed: \n${errors.joinToString("\n")}" }
}

fun File.copyRecursivelyWithModifications(
    target: File,
    overwrite: Boolean = false,
    modifications: List<Modification>
): List<Modification> {
    fun File.getNestedFilesSorted(): List<File> = this.walk().sortedBy { f -> f.path.substring(this.path.lastIndex) }.toList()

    this.copyRecursively(target, overwrite)
    val oldToNewFiles = this.getNestedFilesSorted().zip(target.getNestedFilesSorted()).toMap()

    return modifications.map { Modification(oldToNewFiles[it.file] ?: error { "No such file ${it.file} found in target" }, it.actions) }
}
