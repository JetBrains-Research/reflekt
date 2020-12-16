package io.reflekt.plugin

import io.reflekt.util.FileUtil.extractAllFiles
import org.gradle.api.artifacts.Configuration
import org.jetbrains.kotlin.gradle.plugin.*
import java.io.File
import io.reflekt.cli.Util.GRADLE_ARTIFACT_ID
import io.reflekt.cli.Util.ENABLED_OPTION_INFO
import io.reflekt.cli.Util.GRADLE_GROUP_ID
import io.reflekt.cli.Util.INTROSPECT_FILE_OPTION_INFO
import io.reflekt.cli.Util.OUTPUT_DIR_OPTION_INFO
import io.reflekt.cli.Util.PLUGIN_ID
import io.reflekt.cli.Util.VERSION
import org.gradle.api.provider.Provider

@Suppress("unused")
class ReflektSubPlugin :  KotlinCompilerPluginSupportPlugin {

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        println("ReflektSubPlugin loaded")
        val project = kotlinCompilation.target.project
        val extension = project.reflekt

        val filesToIntrospect: MutableSet<File> = HashSet()
        project.configurations.filter { it.isCanBeResolved }.forEach {
            filesToIntrospect.addAll(getFilesToIntrospect(getJarFilesToIntrospect(it, extension)))
        }
        val librariesToIntrospect = filesToIntrospect.map { SubpluginOption(key = INTROSPECT_FILE_OPTION_INFO.name, value = it.absolutePath) }
        return project.provider {
            librariesToIntrospect + SubpluginOption(key = ENABLED_OPTION_INFO.name, value = extension.enabled.toString()) + SubpluginOption(key = OUTPUT_DIR_OPTION_INFO.name, value = extension.generationPath)
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
        jarFiles.forEach {
            files.addAll(extractAllFiles(it))
        }
        return files
    }

    private fun getJarFilesToIntrospect(configuration: Configuration, extension: ReflektGradleExtension): Set<File> {
        val jarsToIntrospect: MutableSet<File> = HashSet()
        val filtered = configuration.dependencies.filter { "${it.group}:${it.name}:${it.version}" in extension.librariesToIntrospect }
        // TODO: resolve files
//        jarsToIntrospect.addAll(configuration.files(*filtered.toTypedArray()))
//        println("jarsToIntrospect: $jarsToIntrospect")
        return jarsToIntrospect
    }
}
