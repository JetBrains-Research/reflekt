package org.jetbrains.reflekt.plugin

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisHandlerExtension
import org.jetbrains.reflekt.plugin.analysis.ReflektModuleAnalysisExtension
import org.jetbrains.reflekt.plugin.analysis.models.ReflektContext
import org.jetbrains.reflekt.plugin.generation.ir.ReflektIrGenerationExtension
import org.jetbrains.reflekt.plugin.generation.ir.SmartReflektIrGenerationExtension
import org.jetbrains.reflekt.plugin.utils.PluginConfig
import org.jetbrains.reflekt.plugin.utils.Util.log
import org.jetbrains.reflekt.plugin.utils.Util.messageCollector
import java.io.File

@AutoService(ComponentRegistrar::class)
class ReflektComponentRegistrar(private val isTestConfiguration: Boolean = false) : ComponentRegistrar {
    override fun registerProjectComponents(
        project: MockProject,
        configuration: CompilerConfiguration
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
                reflektMetaFile = config.reflektMetaFileRelativePath?.let { File(it) }
            )
        )
        IrGenerationExtension.registerExtension(
            project,
            ReflektIrGenerationExtension(
                reflektContext = reflektContext,
                messageCollector = config.messageCollector,
                toReplaceIr = !config.toSaveMetadata
            )
        )
        IrGenerationExtension.registerExtension(
            project,
            SmartReflektIrGenerationExtension(
                classpath = config.dependencyJars,
                reflektContext = reflektContext,
                messageCollector = config.messageCollector,
            )
        )
    }
}

