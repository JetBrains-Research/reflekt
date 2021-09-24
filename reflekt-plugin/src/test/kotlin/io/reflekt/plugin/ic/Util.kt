package io.reflekt.plugin.ic

import io.reflekt.plugin.util.MavenLocalUtil.getReflektProjectJars
import io.reflekt.plugin.util.MavenLocalUtil.getStdLibJar
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.incremental.makeIncrementally
import java.io.File

internal fun createCompilerArguments(destinationDir: File, testDir: File, pathToDownloadKotlinSources: File): K2JVMCompilerArguments {
    val reflektJars = getReflektProjectJars().map { it.absolutePath }
    val compilerClasspath = reflektJars.toMutableList()
    compilerClasspath.add(getStdLibJar(pathToDownloadKotlinSources).absolutePath)
    return K2JVMCompilerArguments().apply {
        moduleName = testDir.name
        destination = destinationDir.path
        pluginClasspaths = reflektJars.toTypedArray()
        pluginOptions = arrayOf("plugin:io.reflekt:enabled=true")
        // TODO: should we really add reflekt jars into classpath?
        classpath = compilerClasspath.joinToString(File.pathSeparator)
    }
}


internal fun compile(cacheDir: File, sourceRoots: Iterable<File>, args: K2JVMCompilerArguments): TestCompilationResult {
    val reporter = TestICReporter()
    val messageCollector = TestMessageCollector()
    makeIncrementally(cacheDir, sourceRoots, args, reporter = reporter, messageCollector = messageCollector)
    // TODO: should we compile java sources too?
    return TestCompilationResult(reporter, messageCollector)
}
