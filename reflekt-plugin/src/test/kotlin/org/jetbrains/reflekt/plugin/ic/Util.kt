package org.jetbrains.reflekt.plugin.ic

import org.jetbrains.reflekt.plugin.util.MavenLocalUtil.getReflektProjectJars
import org.jetbrains.reflekt.plugin.util.MavenLocalUtil.getStdLibJar
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.incremental.makeIncrementally
import org.jetbrains.reflekt.plugin.ic.modification.Modification
import java.io.File

internal fun createCompilerArguments(destinationDir: File, testDir: File, pathToDownloadKotlinSources: File): K2JVMCompilerArguments {
    val reflektJars = getReflektProjectJars().map { it.absolutePath }
    val compilerClasspath = reflektJars.plus(testDir).toMutableList()
    compilerClasspath.add(getStdLibJar(pathToDownloadKotlinSources).absolutePath)
    return K2JVMCompilerArguments().apply {
        moduleName = testDir.name
        destination = destinationDir.path
        pluginClasspaths = reflektJars.toTypedArray()
        pluginOptions = arrayOf("plugin:org.jetbrains.reflekt:enabled=true")
        // TODO: should we really add reflekt jars into classpath?
        classpath = compilerClasspath.joinToString(File.pathSeparator)
        jvmTarget = "11"
        useIR = true
    }
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
