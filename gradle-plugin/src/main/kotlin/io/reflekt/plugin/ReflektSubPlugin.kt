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
        println("ReflektSubPlugin loaded")
        val extension = project.extensions.findByType(ReflektGradleExtension::class.java)
            ?: ReflektGradleExtension()

        val filesToIntrospect: MutableSet<File> = HashSet()
        project.configurations.forEach { _ ->
            filesToIntrospect.addAll(getFilesToIntrospect(getJarFilesToIntrospect(project, extension)))
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

    private fun getJarFilesToIntrospect(target: Project, extension: ReflektGradleExtension): Set<File> {
        val filesToIntrospect: MutableSet<File> = HashSet()
        target.configurations.forEach { configuration ->
            val filtered = configuration.dependencies
                .filter { "${it.group}:${it.name}:${it.version}" in extension.librariesToIntrospect }
            filesToIntrospect.addAll(configuration.files(*filtered.toTypedArray()))
        }
        return filesToIntrospect
    }

    /**
     * Just needs to be consistent with the key for ReflektCommandLineProcessor#pluginId
     */
    override fun getCompilerPluginId(): String = "io.reflekt"

    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        groupId = "io.reflekt",
        artifactId = "kotlin-plugin",
        version = "0.0.1"
    )
}
