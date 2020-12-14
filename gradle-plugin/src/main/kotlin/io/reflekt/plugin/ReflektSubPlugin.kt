package io.reflekt.plugin

import com.google.auto.service.AutoService
import io.reflekt.util.FileUtil.extractAllFiles
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.compile.AbstractCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions
import org.jetbrains.kotlin.gradle.plugin.*
import java.io.File
import io.reflekt.cli.Util.ARTIFACT_ID
import io.reflekt.cli.Util.ENABLED_OPTION_INFO
import io.reflekt.cli.Util.GROUP_ID
import io.reflekt.cli.Util.INTROSPECT_FILE_OPTION_INFO
import io.reflekt.cli.Util.OUTPUT_DIR_OPTION_INFO
import io.reflekt.cli.Util.PLUGIN_ID
import io.reflekt.cli.Util.VERSION

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
        project.configurations.filter { it.isCanBeResolved }.forEach {
            filesToIntrospect.addAll(getFilesToIntrospect(getJarFilesToIntrospect(it, extension)))
        }
        val librariesToIntrospect = filesToIntrospect.map { SubpluginOption(key = INTROSPECT_FILE_OPTION_INFO.name, value = it.absolutePath) }
        return librariesToIntrospect + SubpluginOption(key = ENABLED_OPTION_INFO.name, value = extension.enabled.toString()) + SubpluginOption(key = OUTPUT_DIR_OPTION_INFO.name, value = extension.generationPath)
    }

    /**
     * Just needs to be consistent with the key for ReflektCommandLineProcessor#pluginId
     */
    override fun getCompilerPluginId(): String = PLUGIN_ID

    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        groupId = GROUP_ID,
        artifactId = ARTIFACT_ID,
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
//        if (toResolve) {
//            jarsToIntrospect.addAll(configuration.files(*filtered.toTypedArray()))
//        }
        return jarsToIntrospect
    }
}
