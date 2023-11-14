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
import java.io.File

typealias ReflektMetaFilesFromLibrariesMap = HashMap<String, Set<File>>

@Suppress("TooManyFunctions")
class ReflektSubPlugin : KotlinCompilerPluginSupportPlugin {
    private lateinit var reflektMetaFilesFromLibrariesMap: ReflektMetaFilesFromLibrariesMap
    private lateinit var extension: ReflektGradleExtension

    override fun apply(target: Project) {
        extension = target.extensions.create("reflekt", ReflektGradleExtension::class.java)
    }

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        val buildDir = project
            .layout
            .buildDirectory
            .asFile
            .get()
            .absolutePath

        reflektMetaFilesFromLibrariesMap = SerializationUtils.deserializeReflektMetaFilesFromLibrariesMap(buildDir)
        val reflektMetaFilesFromLibraries = if (extension.toSaveMetadata) emptyList() else project.getReflektMetaFilesFromLibraries()
        val reflektMetaFilesFromLibrariesOptions =
            reflektMetaFilesFromLibraries.map { SubpluginOption(key = REFLEKT_META_FILE_OPTION_INFO.name, value = it.absolutePath) }
        val dependencyJars = project.configurations.first { it.name == "compileClasspath" }
            .map { SubpluginOption(key = DEPENDENCY_JAR_OPTION_INFO.name, value = it.absolutePath) }

        val generationPath = "$buildDir/${extension.generationPath}"

        with(project) {
            afterEvaluate {
                // TODO: check if it works correctly
                project.sourceSets.apply {
                    getAt("main").allSource.srcDir(generationPath)
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

    // TODO: should we scan all dependencies or the user can set up it?
    private fun Project.getReflektMetaFilesFromLibraries(): MutableSet<File> = configurations.asSequence()
        .filter { it.isCanBeResolved }
        .filterNot { "kotlinCompiler" in it.name }  // We should miss all kotlinCompiler configuration since they will be resolved later by the Kotlin compiler
        .flatMapTo(HashSet()) {
            reflektMetaFilesFromLibrariesMap.getOrPut(it.name) { getReflektMetaFiles(getJarFilesToIntrospect(it)) }
        }

    private fun createReflektMeta(resourcesDir: String): File {
        val metaInfDir = File("$resourcesDir/$META_INF_DIR")
        metaInfDir.mkdirs()
        return File(metaInfDir, REFLEKT_META_FILE)
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

    private fun Project.getReflektMetaFile(jarFile: File) = zipTree(jarFile).files.find { it.name == REFLEKT_META_FILE }

    private fun Project.getReflektMetaFiles(jarFiles: Set<File>): Set<File> =
        jarFiles.mapNotNull { getLibJarWithoutSources(it) }.mapNotNull { getReflektMetaFile(it) }.toSet()

    private fun getLibJarWithoutSources(jarFile: File): File? {
        val jarName = "${jarFile.name.substringBeforeLast('.', "")}.jar"
        // TODO: make sure each jar has the same file structure and it's safe to call jarFile.parentFile.parentFile.listFiles()
        return jarFile
            .parentFile
            .parentFile
            .listFiles()
            ?.firstOrNull { it.isDirectory }
            ?.listFiles()
            ?.find { it.name == jarName }
    }

    @Suppress("SpreadOperator")
    private fun getJarFilesToIntrospect(configuration: Configuration): Set<File> =
        configuration.files(*configuration.dependencies.toTypedArray()).toSet()

    private companion object {
        private const val META_INF_DIR = "META-INF"
        private const val REFLEKT_META_FILE = "ReflektMeta"
    }
}
