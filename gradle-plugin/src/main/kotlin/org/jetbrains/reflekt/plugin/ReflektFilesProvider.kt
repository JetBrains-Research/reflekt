package org.jetbrains.reflekt.plugin

import org.jetbrains.reflekt.util.file.extractAllFiles

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration

import java.io.File

object ReflektFilesProvider {
    private const val REFLEKT_META_FILE = "ReflektMeta"
    private const val META_INF_DIR = "META-INF"

    fun createMetaFile(project: Project): File {
        val resourcesDir = project.getResourcesPath()
        val metaInfDir = File("$resourcesDir/$META_INF_DIR")
        metaInfDir.mkdirs()
        return File("${metaInfDir.path}/$REFLEKT_META_FILE")
    }

    @Suppress("ForbiddenComment")
    // TODO: can we do it better?
    // take a look at project.mySourceSets.getAt("main").resources.first().absolutePath
    private fun Project.getResourcesPath(): String = "${project.rootDir}${project.path.replace(":", "/")}/src/main/resources"

    fun getLibrariesMetaFiles(configuration: Configuration, extension: ReflektGradleExtension): List<File> {
        val jarFiles = getJarFilesToIntrospect(configuration, extension).mapNotNull { getLibJarWithoutSources(it) }
        return jarFiles.map { extractReflektMetaFile(it) }
    }

    private fun extractReflektMetaFile(jarFile: File) =
        jarFile.extractAllFiles().find { it.name == REFLEKT_META_FILE } ?: error("Jar file ${jarFile.absolutePath} does not have $REFLEKT_META_FILE file!")

    private fun getLibJarWithoutSources(jarFile: File): File? {
        val jarName = "${jarFile.name.substringBeforeLast('.', "")}.jar"
        // TODO: make sure each jar has the same file structure and it's safe to call jarFile.parentFile.parentFile.listFiles()
        jarFile.parentFile.parentFile.listFiles()?.filter { it.isDirectory }?.forEach { folder ->
            folder.listFiles()?.find { it.name == jarName }.let {
                return it
            }
        }
        return null
    }

    private fun getJarFilesToIntrospect(configuration: Configuration, extension: ReflektGradleExtension): Set<File> {
        val jarsToIntrospect: MutableSet<File> = HashSet()
        val librariesToIntrospect = configuration.dependencies.filter { "${it.group}:${it.name}:${it.version}" in extension.librariesToIntrospect }
        val librariesNames = librariesToIntrospect.map { it.name }
        if (librariesToIntrospect.isNotEmpty()) {
            @Suppress("IDENTIFIER_LENGTH")
            require(configuration.isCanBeResolved) { "The parameter canBeResolve must be true!" }
            @Suppress("SpreadOperator")
            jarsToIntrospect.addAll(configuration.files(*librariesToIntrospect.toTypedArray()).toSet().filter { f ->
                // TODO: check if it's okay to add files based on libraries' names in their paths
                librariesNames.any { it in f.path }
            })
        }
        return jarsToIntrospect
    }
}
