package io.reflekt.plugin

import io.reflekt.plugin.analysis.ReflektModuleAnalysisExtension
import io.reflekt.plugin.analysis.models.ReflektContext
import io.reflekt.plugin.generation.ir.ReflektIrGenerationExtension
import io.reflekt.plugin.generation.ir.SmartReflektIrGenerationExtension
import io.reflekt.plugin.utils.PluginConfig
import io.reflekt.plugin.utils.Util.log
import io.reflekt.plugin.utils.Util.messageCollector

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisHandlerExtension

import java.io.File

@AutoService(ComponentRegistrar::class)
class ReflektComponentRegistrar(private val isTestConfiguration: Boolean = false) : ComponentRegistrar {
    override fun registerProjectComponents(
        project: MockProject,
        configuration: CompilerConfiguration,
    ) {
        val config = PluginConfig(configuration, isTestConfiguration = isTestConfiguration)
        val reflektContext = ReflektContext()

        configuration.messageCollector.log("PROJECT FILE PATH: ${project.projectFilePath}")

        // This will be called multiple times (for each project module),
        // since compilation process runs module by module
        AnalysisHandlerExtension.registerExtension(
            project,
            ReflektModuleAnalysisExtension(
                reflektMetaFiles = config.reflektMetaFilesFromLibraries,
                toSaveMetadata = config.toSaveMetadata,
                generationPath = config.outputDir,
                reflektContext = reflektContext,
                messageCollector = config.messageCollector,
                reflektMetaFile = config.reflektMetaFileRelativePath?.let { File(it) },
            ),
        )
        IrGenerationExtension.registerExtension(
            project,
            ReflektIrGenerationExtension(
                reflektContext = reflektContext,
                messageCollector = config.messageCollector,
                toReplaceIr = !config.toSaveMetadata,
            ),
        )
        IrGenerationExtension.registerExtension(
            project,
            SmartReflektIrGenerationExtension(
                classpath = config.dependencyJars,
                reflektContext = reflektContext,
                messageCollector = config.messageCollector,
            ),
        )
    }
}
