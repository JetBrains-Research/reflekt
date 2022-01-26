package org.jetbrains.reflekt.plugin
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.reflekt.plugin.ReflektFilesProvider.createMetaFile
import org.jetbrains.reflekt.plugin.ReflektFilesProvider.getLibrariesMetaFiles
import org.jetbrains.reflekt.plugin.util.kotlin
import org.jetbrains.reflekt.plugin.util.mySourceSets
import org.jetbrains.reflekt.util.Util.DEPENDENCY_JAR_OPTION_INFO
import org.jetbrains.reflekt.util.Util.ENABLED_OPTION_INFO
import org.jetbrains.reflekt.util.Util.GRADLE_ARTIFACT_ID
import org.jetbrains.reflekt.util.Util.GRADLE_GROUP_ID
import org.jetbrains.reflekt.util.Util.LIBRARY_TO_INTROSPECT
import org.jetbrains.reflekt.util.Util.OUTPUT_DIR_OPTION_INFO
import org.jetbrains.reflekt.util.Util.PLUGIN_ID
import org.jetbrains.reflekt.util.Util.REFLEKT_META_FILE_OPTION_INFO
import org.jetbrains.reflekt.util.Util.REFLEKT_META_FILE_PATH
import org.jetbrains.reflekt.util.Util.SAVE_METADATA_OPTION_INFO
import org.jetbrains.reflekt.util.Util.VERSION
import java.io.File


@Suppress("unused")
class ReflektSubPlugin : KotlinCompilerPluginSupportPlugin {

    override fun apply(target: Project): Unit = target.run {
        pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
            configure<KotlinJvmProjectExtension> {
                sourceSets["main"].apply {
                    dependencies {
                        implementation("org.jetbrains.reflekt:reflekt-dsl:${VERSION}")
                    }
                }
            }

            tasks.withType<KotlinCompile> {
                kotlinOptions {
                    useIR = true
                    languageVersion = "1.5"
                    apiVersion = "1.5"
                    jvmTarget = "11"
                    // Current Reflekt version does not support incremental compilation process
                    incremental = false
                }
            }

        }
    }

    @Suppress("TYPE_ALIAS")
    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        println("ReflektSubPlugin loaded")
        val project = kotlinCompilation.target.project
        val extension = project.reflekt

        val reflektMetaFiles: MutableSet<File> = HashSet()
        project.configurations.forEach {
            reflektMetaFiles.addAll(getLibrariesMetaFiles(it, extension))
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
                SubpluginOption(key = REFLEKT_META_FILE_PATH.name, value = createMetaFile(project).absolutePath)
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
        version = VERSION,
    )
}
