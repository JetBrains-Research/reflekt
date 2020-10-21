package io.reflekt.plugin

import com.google.auto.service.AutoService
import org.gradle.api.Project
import org.gradle.api.tasks.compile.AbstractCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinGradleSubplugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

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
        val extension = project.extensions.findByType(ReflektGradleExtension::class.java)
            ?: ReflektGradleExtension()

        val filteredLibraries: MutableSet<String> = HashSet()
        project.configurations.forEach { configuration ->
            filteredLibraries.addAll(configuration.dependencies.map { "${it.group}:${it.name}:${it.version}" }.intersect(extension.librariesToIntrospect))
        }
        val librariesToIntrospect = filteredLibraries.map { SubpluginOption(key = "libraryToIntrospect", value = it) }
        return librariesToIntrospect + SubpluginOption(key = "enabled", value = extension.enabled.toString())
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
