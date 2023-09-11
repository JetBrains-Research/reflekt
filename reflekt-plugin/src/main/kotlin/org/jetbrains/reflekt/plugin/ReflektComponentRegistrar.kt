@file:Suppress("FILE_UNORDERED_IMPORTS")

package org.jetbrains.reflekt.plugin

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.*
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.reflekt.plugin.analysis.analyzer.IrInstancesAnalyzer
import org.jetbrains.reflekt.plugin.analysis.collector.ir.*
import org.jetbrains.reflekt.plugin.analysis.models.ir.*
import org.jetbrains.reflekt.plugin.analysis.models.isNotEmpty
import org.jetbrains.reflekt.plugin.generation.ReflektMetaFileGenerator
import org.jetbrains.reflekt.plugin.generation.code.generator.ReflektImplGeneratorExtension
import org.jetbrains.reflekt.plugin.generation.ir.ReflektIrGenerationExtension
import org.jetbrains.reflekt.plugin.generation.ir.SmartReflektIrGenerationExtension
import org.jetbrains.reflekt.plugin.utils.PluginConfig
import java.io.File

/**
 * Registers the plugin and applies it to the project.
 * We have two main cases to interact with Reflekt:
 *  a) the project case,
 *  b) the library case.
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
@OptIn(ExperimentalCompilerApi::class)
@Suppress("TOO_LONG_FUNCTION")
class ReflektComponentRegistrar(private val isTestConfiguration: Boolean = false) : CompilerPluginRegistrar() {
    override val supportsK2: Boolean = false

    /**
     * Tne main plugin's function that parses all compiler arguments and runs all Kotlin compiler's extensions.
     * All extensions will be called multiple times (for each project module),
     * since compilation process runs module by module
     *
     * @param configuration current compiler configuration, also stores all parsed options from the [ReflektCommandLineProcessor]
     */
    override fun ExtensionStorage.registerExtensions(
        configuration: CompilerConfiguration,
    ) {
        val config = PluginConfig(configuration, isTestConfiguration = isTestConfiguration)

        val instancesAnalyzer = IrInstancesAnalyzer()
        val libraryArgumentsWithInstances = LibraryArgumentsWithInstances()
        // Collect IR instances for classes, objects, and functions
        IrGenerationExtension.registerExtension(
            InstancesCollectorExtension(
                irInstancesAnalyzer = instancesAnalyzer,
                reflektMetaFilesFromLibraries = config.reflektMetaFilesFromLibraries,
                libraryArgumentsWithInstances = libraryArgumentsWithInstances,
                messageCollector = config.messageCollector,
            ),
        )

        @Suppress("COMMENT_WHITE_SPACE")
        // TODO: separate cases and accept use Reflekt in both cases in the same time?
        //  e.g., a part of queries for the current project and IR replacement
        //  and another part with ReflektImpl approach??
        // The Reflekt compiler plugin considers two possible scenarios.
        // (I) The first scenario is searching for entities in a project that uses Reflekt.
        // It can also run additional steps, see II.b).
        // (II) The second one is searching for entities from a library that is imported in a project.
        //  This scenario has two steps:
        //    II.a) Scan files in the library during it's compilation,
        //    extract all Reflekt queries and entities fqNames and save them into the ReflektMeta file.
        //    On this step we don't replace IR for the Reflekt queries.
        //    II.b) Run the first scenario during the project compilation, and
        //    also extract the libraries queries from the II.a) step to handle them.
        //    All libraries queries are handled separately: we generate a special ReflektImpl
        //    file installed of IR replacement for these queries.
        if (config.toSaveMetadata) {
            // Handle II.a) case
            collectAndStoreReflektArguments(config, instancesAnalyzer)
        } else {
            // Handle I case
            replaceReflektQueries(config, instancesAnalyzer, libraryArgumentsWithInstances)
        }
    }

    private fun ExtensionStorage.collectAndStoreReflektArguments(
        config: PluginConfig,
        instancesAnalyzer: IrInstancesAnalyzer,
    ) {
        val reflektQueriesArguments = LibraryArguments()
        IrGenerationExtension.registerExtension(
            ReflektArgumentsCollectorExtension(
                irInstancesAnalyzer = instancesAnalyzer,
                reflektQueriesArguments = reflektQueriesArguments,
                messageCollector = config.messageCollector,
            ),
        )
        val reflektMetaFile = config.reflektMetaFileRelativePath?.let { File(it) } ?: error("reflektMetaFileRelativePath is null for the project")
        IrGenerationExtension.registerExtension(
            ReflektMetaFileGenerator(
                instancesAnalyzer,
                reflektQueriesArguments,
                reflektMetaFile,
                config.messageCollector,
            ),
        )
    }

    private fun ExtensionStorage.replaceReflektQueries(
        config: PluginConfig,
        instancesAnalyzer: IrInstancesAnalyzer,
        libraryArgumentsWithInstances: LibraryArgumentsWithInstances,
    ) {
        // Extract reflekt arguments from external libraries
        IrGenerationExtension.registerExtension(
            ExternalLibraryInstancesCollectorExtension(
                irInstancesAnalyzer = instancesAnalyzer,
                irInstancesIds = libraryArgumentsWithInstances.instances,
            ),
        )
        this.generateReflektImpl(config, instancesAnalyzer, libraryArgumentsWithInstances.libraryArguments)
        IrGenerationExtension.registerExtension(
            ReflektIrGenerationExtension(
                irInstancesAnalyzer = instancesAnalyzer,
                libraryArguments = libraryArgumentsWithInstances.libraryArguments,
                messageCollector = config.messageCollector,
            ),
        )

        IrGenerationExtension.registerExtension(
            SmartReflektIrGenerationExtension(
                irInstancesAnalyzer = instancesAnalyzer,
                classpath = config.dependencyJars,
                messageCollector = config.messageCollector,
            ),
        )
    }

    private fun ExtensionStorage.generateReflektImpl(
        config: PluginConfig,
        instancesAnalyzer: IrInstancesAnalyzer,
        libraryArguments: LibraryArguments,
    ) {
        if (libraryArguments.isNotEmpty() && config.outputDir == null) {
            error("The output path for the ReflektImpl file was not specified")
        }
        val libraryQueriesResults = LibraryQueriesResults.fromLibraryArguments(libraryArguments)
        IrGenerationExtension.registerExtension(
            LibraryQueriesResultsCollector(
                irInstancesAnalyzer = instancesAnalyzer,
                libraryQueriesResults = libraryQueriesResults,
                messageCollector = config.messageCollector,
            ),
        )
        IrGenerationExtension.registerExtension(
            ReflektImplGeneratorExtension(
                libraryQueriesResults = libraryQueriesResults,
                generationPath = config.outputDir!!,
                messageCollector = config.messageCollector,
            ),
        )
    }
}
