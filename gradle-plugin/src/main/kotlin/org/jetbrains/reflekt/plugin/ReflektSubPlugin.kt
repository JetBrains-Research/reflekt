package org.jetbrains.reflekt.plugin

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.reflekt.plugin.util.*
import org.jetbrains.reflekt.util.Util.DEPENDENCY_JAR_OPTION_INFO
import org.jetbrains.reflekt.util.Util.ENABLED_OPTION_INFO
import org.jetbrains.reflekt.util.Util.GRADLE_ARTIFACT_ID
import org.jetbrains.reflekt.util.Util.GRADLE_GROUP_ID
import org.jetbrains.reflekt.util.Util.OUTPUT_DIR_OPTION_INFO
import org.jetbrains.reflekt.util.Util.PLUGIN_ID
import org.jetbrains.reflekt.util.Util.REFLEKT_META_FILE_OPTION_INFO
import org.jetbrains.reflekt.util.Util.REFLEKT_META_FILE_PATH
import org.jetbrains.reflekt.util.Util.SAVE_METADATA_OPTION_INFO
import org.jetbrains.reflekt.util.Util.VERSION
import org.jetbrains.reflekt.util.file.extractAllFiles
import java.io.File

typealias ReflektMetaFilesFromLibrariesMap = HashMap<String, Set<File>>

@Suppress("TooManyFunctions")
class ReflektSubPlugin : KotlinCompilerPluginSupportPlugin {
    private val reflektMetaFile = "ReflektMeta"
    private val metaInfDir = "META-INF"
    private lateinit var reflektMetaFilesFromLibrariesMap: ReflektMetaFilesFromLibrariesMap

    @Suppress("TYPE_ALIAS")
    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        println("ReflektSubPlugin loaded")
        val project = kotlinCompilation.target.project
        val extension = project.reflekt
        val buildDir = project.buildDir.absolutePath

        reflektMetaFilesFromLibrariesMap = SerializationUtils.deserializeReflektMetaFilesFromLibrariesMap(buildDir)
        val reflektMetaFilesFromLibraries = if (extension.toSaveMetadata) emptyList() else project.getReflektMetaFilesFromLibraries()
        val reflektMetaFilesFromLibrariesOptions =
            reflektMetaFilesFromLibraries.map { SubpluginOption(key = REFLEKT_META_FILE_OPTION_INFO.name, value = it.absolutePath) }
        val dependencyJars = project.configurations.first { it.name == "compileClasspath" }
            .map { SubpluginOption(key = DEPENDENCY_JAR_OPTION_INFO.name, value = it.absolutePath) }

        val generationPath = "$buildDir/${extension.generationPath}"

        with(project) {
            afterEvaluate {
                project.sourceSets.apply {
                    this.getAt("main").kotlin.srcDir(generationPath)
                }
            }
        }
        SerializationUtils.serializeReflektMetaFilesFromLibrariesMap(reflektMetaFilesFromLibrariesMap, buildDir)

        return project.provider {
            reflektMetaFilesFromLibrariesOptions + dependencyJars +
                SubpluginOption(key = ENABLED_OPTION_INFO.name, value = extension.enabled.toString()) +
                SubpluginOption(key = OUTPUT_DIR_OPTION_INFO.name, value = generationPath) +
                SubpluginOption(key = SAVE_METADATA_OPTION_INFO.name, value = extension.toSaveMetadata.toString()) +
                SubpluginOption(key = REFLEKT_META_FILE_PATH.name, value = createReflektMeta(project.getResourcesPath()).absolutePath)
        }
    }

    // TODO: should we scan all dependencies or the user can set it up?
    private fun Project.getReflektMetaFilesFromLibraries(): MutableSet<File> {
        val reflektMetaFilesFromLibraries: MutableSet<File> = HashSet()
        // We should skip all kotlinCompiler configuration since they will be resolved later by the Kotlin compiler
        for (conf in this.configurations.filter { it.isCanBeResolved && !("kotlinCompiler" in it.name) }) {
            val files = reflektMetaFilesFromLibrariesMap.getOrPut(conf.name) { getReflektMetaFiles(getJarFilesToIntrospect(conf)) }
            reflektMetaFilesFromLibraries.addAll(files)
        }
        return reflektMetaFilesFromLibraries
    }

    private fun createReflektMeta(resourcesDir: String): File {
        val metaInfDir = File("$resourcesDir/$metaInfDir")
        metaInfDir.mkdirs()
        return File("${metaInfDir.path}/$reflektMetaFile")
    }

    @Suppress("ForbiddenComment")
    // TODO: can we do it better?
    // take a look at project.mySourceSets.getAt("main").resources.first().absolutePath
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

    private fun getReflektMetaFile(jarFile: File) = jarFile.extractAllFiles().find { it.name == reflektMetaFile }

    private fun getReflektMetaFiles(jarFiles: Set<File>): Set<File> {
        val files: MutableSet<File> = mutableSetOf()
        for (jar in jarFiles) {
            getLibJarWithoutSources(jar)?.let { f ->
                getReflektMetaFile(f)?.let {
                    files.add(it)
                }
            }
        }
        return files
    }

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

    @Suppress("SpreadOperator")
    private fun getJarFilesToIntrospect(configuration: Configuration) =
        configuration.files(*configuration.dependencies.toTypedArray()).toSet()
}
