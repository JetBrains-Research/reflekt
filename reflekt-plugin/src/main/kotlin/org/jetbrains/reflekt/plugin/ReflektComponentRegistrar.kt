package org.jetbrains.reflekt.plugin

import org.jetbrains.reflekt.plugin.analysis.ReflektModuleAnalysisExtension
import org.jetbrains.reflekt.plugin.analysis.analyzer.ir.IrInstancesAnalyzer
import org.jetbrains.reflekt.plugin.analysis.collector.ir.InstancesCollectorExtension
import org.jetbrains.reflekt.plugin.analysis.models.ir.IrReflektContext
import org.jetbrains.reflekt.plugin.generation.ir.*
import org.jetbrains.reflekt.plugin.utils.PluginConfig
import org.jetbrains.reflekt.plugin.utils.Util.log
import org.jetbrains.reflekt.plugin.utils.Util.messageCollector

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.resolve.extensions.AnalysisHandlerExtension

import java.io.File

/**
 * Registers the plugin and applies it to the project.
 *
 * @property isTestConfiguration indicates if the plugin is used in tests
 */
@AutoService(ComponentRegistrar::class)
@Suppress("TOO_LONG_FUNCTION")
// TODO: delete unnecessary extensions
class ReflektComponentRegistrar(private val isTestConfiguration: Boolean = false) : ComponentRegistrar {
    /**
     * Tne main plugin's function that parses all compiler arguments and runs all Kotlin compiler's extensions.
     *
     * @param project current project
     * @param configuration current compiler configuration, also stores all parsed options form the [ReflektCommandLineProcessor]
     */
    override fun registerProjectComponents(
        project: MockProject,
        configuration: CompilerConfiguration,
    ) {
        val config = PluginConfig(configuration, isTestConfiguration = isTestConfiguration)
        val reflektContext = IrReflektContext()

        configuration.messageCollector.log("PROJECT FILE PATH: ${project.projectFilePath}")

        // This will be called multiple times (for each project module),
        // since compilation process runs module by module
        AnalysisHandlerExtension.registerExtension(
            project,
            ReflektModuleAnalysisExtension(
                reflektMetaFilesFromLibraries = config.reflektMetaFilesFromLibraries,
                toSaveMetadata = config.toSaveMetadata,
                generationPath = config.outputDir,
                reflektContext = reflektContext,
                messageCollector = config.messageCollector,
                reflektMetaFile = config.reflektMetaFileRelativePath?.let { File(it) },
            ),
        )

        // Collect IR instances for classes, objects, and functions
        val instancesAnalyzer = IrInstancesAnalyzer()
        IrGenerationExtension.registerExtension(
            project,
            InstancesCollectorExtension(
                irInstancesAnalyzer = instancesAnalyzer,
                messageCollector = config.messageCollector,
            ),
        )

        // TODO: Move all Reflekt parts to backend
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
                irInstancesAnalyzer = instancesAnalyzer,
                classpath = config.dependencyJars,
                messageCollector = config.messageCollector,
            ),
        )
    }
}
