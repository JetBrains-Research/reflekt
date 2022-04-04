package org.jetbrains.reflekt.plugin

import org.jetbrains.reflekt.plugin.analysis.analyzer.ir.IrInstancesAnalyzer
import org.jetbrains.reflekt.plugin.analysis.models.ir.LibraryArguments
import org.jetbrains.reflekt.plugin.generation.ReflektMetaFileGenerator
import org.jetbrains.reflekt.plugin.generation.ir.ReflektIrGenerationExtension
import org.jetbrains.reflekt.plugin.generation.ir.SmartReflektIrGenerationExtension
import org.jetbrains.reflekt.plugin.utils.PluginConfig
import org.jetbrains.reflekt.plugin.utils.Util.log
import org.jetbrains.reflekt.plugin.utils.Util.messageCollector

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.reflekt.plugin.analysis.collector.ir.*
import org.jetbrains.reflekt.plugin.analysis.models.ir.LibraryArgumentsWithInstances
import org.jetbrains.reflekt.plugin.analysis.serialization.SerializationUtils

import java.io.File

/**
 * Registers the plugin and applies it to the project.
 * We have two main cases to interact with Reflekt:
 *  a) the project case
 *  b) the library case
 *
 * The project case means that we search for Reflekt and SmartReflekt queries only in the current project,
 *  and replace their IR.
 * The library case means that we search for Reflekt queries (SmartReflekt queries are not supported yet) in this project,
 *  but don't replace their IR and save ReflektMeta file to the META-INF folder of the current project.
 *  Next, when another project include this project as a library we extract all information from the ReflektMeta file,
 *  and next for these queries will generate the ReflektImpl.kt file with the results for these queries.
 *  It allows replacing run-time reflection even for libraries where we don't know the search result during its compilation,
 *  but we know the full information during compilation the project with this library.
 *
 * @property isTestConfiguration indicates if the plugin is used in tests
 */
@AutoService(ComponentRegistrar::class)
@Suppress("TOO_LONG_FUNCTION")
// TODO: delete unnecessary extensions
class ReflektComponentRegistrar(private val isTestConfiguration: Boolean = false) : ComponentRegistrar {
    /**
     * Tne main plugin's function that parses all compiler arguments and runs all Kotlin compiler's extensions.
     * All extensions will be called multiple times (for each project module),
     * since compilation process runs module by module
     *
     * @param project current project
     * @param configuration current compiler configuration, also stores all parsed options form the [ReflektCommandLineProcessor]
     */
    override fun registerProjectComponents(
        project: MockProject,
        configuration: CompilerConfiguration,
    ) {
        val config = PluginConfig(configuration, isTestConfiguration = isTestConfiguration)
        configuration.messageCollector.log("PROJECT FILE PATH: ${project.projectFilePath}")

        // Collect IR instances for classes, objects, and functions
        val instancesAnalyzer = IrInstancesAnalyzer()
        val libraryArgumentsWithInstances = LibraryArgumentsWithInstances()
        IrGenerationExtension.registerExtension(
            project,
            InstancesCollectorExtension(
                irInstancesAnalyzer = instancesAnalyzer,
                reflektMetaFilesFromLibraries = config.reflektMetaFilesFromLibraries,
                libraryArgumentsWithInstances = libraryArgumentsWithInstances,
                messageCollector = config.messageCollector,
            ),
        )
        IrGenerationExtension.registerExtension(
            project,
            LibraryInstancesCollectorExtension(
                irInstancesAnalyzer = instancesAnalyzer,
                irInstancesFqNames = libraryArgumentsWithInstances.instances,
            ),
        )

        // TODO: separate cases and accept use Reflekt in both cases in the same time?
        //  e.g. a part of queries for the current project and IR replacement
        //  and another part with ReflektImpl approach??
        if (config.toSaveMetadata) {
            project.registerLibraryExtensions(config, instancesAnalyzer)
        } else {
            project.registerProjectExtensions(config, instancesAnalyzer, libraryArgumentsWithInstances.libraryArguments)
        }
    }

    private fun MockProject.registerLibraryExtensions(
        config: PluginConfig,
        instancesAnalyzer: IrInstancesAnalyzer
    ) {
        val reflektQueriesArguments = LibraryArguments()
        IrGenerationExtension.registerExtension(
            this,
            ReflektArgumentsCollectorExtension(
                irInstancesAnalyzer = instancesAnalyzer,
                reflektQueriesArguments = reflektQueriesArguments,
                messageCollector = config.messageCollector,
            ),
        )
        val reflektMetaFile = config.reflektMetaFileRelativePath?.let { File(it) } ?: error("reflektMetaFileRelativePath is null for the project")
        IrGenerationExtension.registerExtension(
            this,
            ReflektMetaFileGenerator(
                instancesAnalyzer,
                reflektQueriesArguments,
                reflektMetaFile,
                config.messageCollector,
            ),
        )
    }

    private fun MockProject.registerProjectExtensions(
        config: PluginConfig,
        instancesAnalyzer: IrInstancesAnalyzer,
        libraryArguments: LibraryArguments,
    ) {
        // TODO: generate ReflektImpl for libraries queries from ReflektMeta
        IrGenerationExtension.registerExtension(
            this,
            ReflektIrGenerationExtension(
                irInstancesAnalyzer = instancesAnalyzer,
                libraryArguments = libraryArguments,
                messageCollector = config.messageCollector,
            ),
        )

        IrGenerationExtension.registerExtension(
            this,
            SmartReflektIrGenerationExtension(
                irInstancesAnalyzer = instancesAnalyzer,
                classpath = config.dependencyJars,
                messageCollector = config.messageCollector,
            ),
        )
    }
}
