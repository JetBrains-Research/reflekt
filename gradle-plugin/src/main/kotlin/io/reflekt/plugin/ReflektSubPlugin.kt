package io.reflekt.plugin

import io.reflekt.util.Util.DEPENDENCY_JAR_OPTION_INFO
import io.reflekt.util.FileUtil.extractAllFiles
import org.gradle.api.artifacts.Configuration
import org.jetbrains.kotlin.gradle.plugin.*
import java.io.File
import io.reflekt.util.Util.GRADLE_ARTIFACT_ID
import io.reflekt.util.Util.ENABLED_OPTION_INFO
import io.reflekt.util.Util.GRADLE_GROUP_ID
import io.reflekt.util.Util.INTROSPECT_FILE_OPTION_INFO
import io.reflekt.util.Util.OUTPUT_DIR_OPTION_INFO
import io.reflekt.util.Util.PLUGIN_ID
import io.reflekt.util.Util.VERSION
import org.gradle.api.provider.Provider

@Suppress("unused")
class ReflektSubPlugin :  KotlinCompilerPluginSupportPlugin {

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        println("ReflektSubPlugin loaded")
        val project = kotlinCompilation.target.project
        val extension = project.reflekt

        val filesToIntrospect: MutableSet<File> = HashSet()
        project.configurations.forEach {
            filesToIntrospect.addAll(getFilesToIntrospect(getJarFilesToIntrospect(it, extension)))
        }
        val librariesToIntrospect = filesToIntrospect.map { SubpluginOption(key = INTROSPECT_FILE_OPTION_INFO.name, value = it.absolutePath) }
        val dependencyJars = project.configurations.first { it.name == "compileClasspath" }
            .map { SubpluginOption(key = DEPENDENCY_JAR_OPTION_INFO.name, value = it.absolutePath) }

        return project.provider {
            librariesToIntrospect + dependencyJars +
                SubpluginOption(key = ENABLED_OPTION_INFO.name, value = extension.enabled.toString()) +
                SubpluginOption(key = OUTPUT_DIR_OPTION_INFO.name, value = extension.generationPath)
        }
    }

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = kotlinCompilation.platformType == KotlinPlatformType.jvm

    /**
     * Just needs to be consistent with the key for ReflektCommandLineProcessor#pluginId
     */
    override fun getCompilerPluginId(): String = PLUGIN_ID

    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        groupId = GRADLE_GROUP_ID,
        artifactId = GRADLE_ARTIFACT_ID,
        version = VERSION
    )

    private fun getFilesToIntrospect(jarFiles: Set<File>): List<File> {
        val files: MutableList<File> = ArrayList()
        jarFiles.forEach { jar ->
            getSourceJar(jar)?.let {
                files.addAll(extractAllFiles(it))
            }
        }
        return files
    }

    private fun getSourceJar(jarFile: File) : File? {
        val sourceName = "${jarFile.name.substringBeforeLast('.', "")}-sources.jar"
        jarFile.parentFile.parentFile.listFiles()?.filter{ it.isDirectory }?.forEach { folder ->
            val sources = folder.listFiles()?.find { it.name == sourceName }
            if (sources != null) {
                return sources
            }
        }
        return null
    }

    private fun getJarFilesToIntrospect(configuration: Configuration, extension: ReflektGradleExtension): Set<File> {
        val jarsToIntrospect: MutableSet<File> = HashSet()
        println("librariesToIntrospect: ${extension.librariesToIntrospect}")
        val filtered = configuration.dependencies.filter { "${it.group}:${it.name}:${it.version}" in extension.librariesToIntrospect }
        val librariesNames = filtered.map { it.name }
        if (filtered.isNotEmpty()) {
            println("filtered: ${filtered}")
            require(configuration.isCanBeResolved) { "The parameter canBeResolve must be true!" }
            // TODO: filter only kotless files
            jarsToIntrospect.addAll(configuration.files(*filtered.toTypedArray()).toSet().filter { f ->
                librariesNames.any { it in f.path }
            })
        }
        println("jarsToIntrospect: $jarsToIntrospect")
        return jarsToIntrospect
    }
}
