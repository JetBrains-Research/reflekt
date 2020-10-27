package io.reflekt.plugin

import com.google.auto.service.AutoService
import io.reflekt.util.FileUtil.extractAllFiles
import org.gradle.api.Project
import org.gradle.api.tasks.compile.AbstractCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinGradleSubplugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import org.gradle.api.artifacts.Configuration
import java.io.File

@AutoService(KotlinGradleSubplugin::class)
class ReflektSubPlugin : KotlinGradleSubplugin<AbstractCompile> {

    override fun isApplicable(project: Project, task: AbstractCompile) =
        project.plugins.hasPlugin(ReflektPlugin::class.java)

    override fun apply(
        project: Project,
        kotlinCompile: AbstractCompile,
        javaCompile: AbstractCompile?,
        variantData: Any?,
        androidProjectHandler: Any?,
        kotlinCompilation: KotlinCompilation<KotlinCommonOptions>?
    ): List<SubpluginOption> {
        println("Reflekt gradle sub plugin loaded")
        val extension = project.extensions.findByType(ReflektGradleExtension::class.java)
            ?: ReflektGradleExtension()

        val filesToIntrospect: MutableSet<File> = HashSet()
        project.configurations.filter { it.isCanBeResolved }.forEach {
            filesToIntrospect.addAll(getFilesToIntrospect(getJarFilesToIntrospect(it, extension)))
        }
        val librariesToIntrospect = filesToIntrospect.map { SubpluginOption(key = "fileToIntrospect", value = it.absolutePath) }
        return librariesToIntrospect + SubpluginOption(key = "enabled", value = extension.enabled.toString())
    }

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
        jarsToIntrospect.addAll(configuration.files(*filtered.toTypedArray()))
        return jarsToIntrospect
    }

    /**
     * Just needs to be consistent with the key for ReflektCommandLineProcessor#pluginId
     */
    override fun getCompilerPluginId(): String = "io.reflekt"

    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        groupId = "io.reflekt",
        /**
         * Just needs to be consistent with the artifactId in reflekt-plugin build.gradle.kts#publishJar
         */
        artifactId = "reflekt-compiler-plugin",
        // Todo: get version from a variable
        version = "0.1.0"
    )
}
