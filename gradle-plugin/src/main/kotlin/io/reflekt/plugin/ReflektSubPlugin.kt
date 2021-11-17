package io.reflekt.plugin

import io.reflekt.plugin.util.kotlin
import io.reflekt.plugin.util.mySourceSets
import io.reflekt.util.Util.DEPENDENCY_JAR_OPTION_INFO
import io.reflekt.util.Util.ENABLED_OPTION_INFO
import io.reflekt.util.Util.GRADLE_ARTIFACT_ID
import io.reflekt.util.Util.GRADLE_GROUP_ID
import io.reflekt.util.Util.LIBRARY_TO_INTROSPECT
import io.reflekt.util.Util.OUTPUT_DIR_OPTION_INFO
import io.reflekt.util.Util.PLUGIN_ID
import io.reflekt.util.Util.REFLEKT_META_FILE_OPTION_INFO
import io.reflekt.util.Util.REFLEKT_META_FILE_PATH
import io.reflekt.util.Util.SAVE_METADATA_OPTION_INFO
import io.reflekt.util.Util.VERSION
import io.reflekt.util.file.extractAllFiles

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.*

import java.io.File

@Suppress("unused")
class ReflektSubPlugin : KotlinCompilerPluginSupportPlugin {
    private val reflektMetaFile = "ReflektMeta"
    private val metaInfDir = "META-INF"

    @Suppress("TYPE_ALIAS")
    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        println("ReflektSubPlugin loaded")
        val project = kotlinCompilation.target.project
        val extension = project.reflekt

        val reflektMetaFiles: MutableSet<File> = HashSet()
        project.configurations.forEach {
            reflektMetaFiles.addAll(getReflektMetaFiles(getJarFilesToIntrospect(it, extension)))
        }
        val reflektMetaFilesOptions = reflektMetaFiles.map { SubpluginOption(key = REFLEKT_META_FILE_OPTION_INFO.name, value = it.absolutePath) }
        val dependencyJars = project.configurations.first { it.name == "compileClasspath" }
            .map { SubpluginOption(key = DEPENDENCY_JAR_OPTION_INFO.name, value = it.absolutePath) }

        val librariesToIntrospect = extension.librariesToIntrospect.map { SubpluginOption(key = LIBRARY_TO_INTROSPECT.name, value = it) }
        val generationPath = "${project.buildDir.absolutePath}/${extension.generationPath}"

        with(project) {
            afterEvaluate {
                project.mySourceSets.apply {
                    this.getAt("main").kotlin.srcDir(generationPath)
                }
            }
        }

        return project.provider {
            librariesToIntrospect + reflektMetaFilesOptions + dependencyJars +
                SubpluginOption(key = ENABLED_OPTION_INFO.name, value = extension.enabled.toString()) +
                SubpluginOption(key = OUTPUT_DIR_OPTION_INFO.name, value = generationPath) +
                SubpluginOption(key = SAVE_METADATA_OPTION_INFO.name, value = extension.toSaveMetadata.toString()) +
                SubpluginOption(key = REFLEKT_META_FILE_PATH.name, value = createReflektMeta(project.getResourcesPath()).absolutePath)
        }
    }

    private fun createReflektMeta(resourcesDir: String): File {
        val metaInfDir = File("$resourcesDir/$metaInfDir")
        if (!metaInfDir.exists()) {
            metaInfDir.mkdirs()
        }
        return File("${metaInfDir.path}/$reflektMetaFile")
    }

    @Suppress("ForbiddenComment")
    // TODO: can we do it better?
    private fun Project.getResourcesPath(): String = "${project.rootDir}${project.path.replace(":", "/")}/src/main/resources"

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = kotlinCompilation.platformType == KotlinPlatformType.jvm

    /**
     * Just needs to be consistent with the key for ReflektCommandLineProcessor#pluginId
     */
    override fun getCompilerPluginId(): String = PLUGIN_ID

    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        groupId = GRADLE_GROUP_ID,
        artifactId = GRADLE_ARTIFACT_ID,
        version = VERSION,
    )

    private fun getReflektMetaFile(jarFile: File) =
        jarFile.extractAllFiles().find { it.name == reflektMetaFile } ?: error("Jar file ${jarFile.absolutePath} does not have $reflektMetaFile file!")

    private fun getReflektMetaFiles(jarFiles: Set<File>): List<File> {
        val files: MutableList<File> = ArrayList()
        jarFiles.forEach { jar ->
            getSourceJar(jar)?.let {
                files.add(getReflektMetaFile(it))
            }
        }
        return files
    }

    private fun getSourceJar(jarFile: File): File? {
        val sourceName = "${jarFile.name.substringBeforeLast('.', "")}.jar"
        jarFile.parentFile.parentFile.listFiles()?.filter { it.isDirectory }?.forEach { folder ->
            val sources = folder.listFiles()?.find { it.name == sourceName }
            sources ?: return sources
        }
        return null
    }

    private fun getJarFilesToIntrospect(configuration: Configuration, extension: ReflektGradleExtension): Set<File> {
        val jarsToIntrospect: MutableSet<File> = HashSet()
        val filtered = configuration.dependencies.filter { "${it.group}:${it.name}:${it.version}" in extension.librariesToIntrospect }
        val librariesNames = filtered.map { it.name }
        if (filtered.isNotEmpty()) {
            @Suppress("IDENTIFIER_LENGTH")
            require(configuration.isCanBeResolved) { "The parameter canBeResolve must be true!" }
            @Suppress("SpreadOperator")
            jarsToIntrospect.addAll(configuration.files(*filtered.toTypedArray()).toSet().filter { f ->
                librariesNames.any { it in f.path }
            })
        }
        return jarsToIntrospect
    }
}
