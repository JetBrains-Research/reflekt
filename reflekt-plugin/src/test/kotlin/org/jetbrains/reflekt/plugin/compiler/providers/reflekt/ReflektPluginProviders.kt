package org.jetbrains.reflekt.plugin.compiler.providers.reflekt

import com.intellij.mock.MockProject
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.*
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.test.frontend.fir.handlers.FirDumpHandler
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.*
import org.jetbrains.reflekt.plugin.ReflektComponentRegistrar
import org.jetbrains.reflekt.plugin.analysis.analyzer.IrInstancesAnalyzer
import org.jetbrains.reflekt.plugin.analysis.collector.ir.*
import org.jetbrains.reflekt.plugin.analysis.models.ir.*
import org.jetbrains.reflekt.plugin.generation.code.generator.ReflektImplGeneratorExtension
import org.jetbrains.reflekt.plugin.util.CodeGenTestPaths
import java.io.File

@OptIn(ExperimentalCompilerApi::class)
class ReflektPluginWithStandaloneProjectProvider(testServices: TestServices) : ReflektPluginProviderBase(testServices) {
    override fun CompilerPluginRegistrar.ExtensionStorage.registerCompilerExtensions(module: TestModule, configuration: CompilerConfiguration) {
        with(ReflektComponentRegistrar(true)) {
            this@registerCompilerExtensions.registerExtensions(configuration)
        }
    }
}

// Provider only for tests to generate ReflektImpl file (without parsing ReflektMeta file)
class ReflektPluginWithLibraryProvider(testServices: TestServices) : ReflektPluginProviderBase(testServices) {
    override fun legacyRegisterCompilerExtensions(project: Project, module: TestModule, configuration: CompilerConfiguration) {
        val instancesAnalyzer = IrInstancesAnalyzer()
        val libraryArgumentsWithInstances = LibraryArgumentsWithInstances()

        // Collect all instances from the project
        IrGenerationExtension.registerExtension(
            project,
            InstancesCollectorExtension(
                irInstancesAnalyzer = instancesAnalyzer,
                reflektMetaFilesFromLibraries = emptySet(),
                libraryArgumentsWithInstances = libraryArgumentsWithInstances,
            )
        )

        // Collect all Reflekt queries arguments
        val reflektQueriesArguments = LibraryArguments()
        IrGenerationExtension.registerExtension(
            project,
            ReflektArgumentsCollectorExtension(
                irInstancesAnalyzer = instancesAnalyzer,
                reflektQueriesArguments = reflektQueriesArguments,
            ),
        )

        // Filter instances according to the Reflekt arguments
        val libraryQueriesResults = LibraryQueriesResults()
        IrGenerationExtension.registerExtension(
            project,
            LibraryQueriesResultsCollectorForTests(
                irInstancesAnalyzer = instancesAnalyzer,
                libraryArguments = reflektQueriesArguments,
                libraryQueriesResults = libraryQueriesResults
            ),
        )

        // Generate RefelktImpl file
        IrGenerationExtension.registerExtension(
            project,
            ReflektImplGeneratorExtension(
                libraryQueriesResults = libraryQueriesResults,
                generationPath = testServices.temporaryDirectoryManager.getOrCreateTempDirectory(TMP_DIRECTORY_NAME),
            ),
        )
    }

    companion object {
        const val TMP_DIRECTORY_NAME = "reflektImplGenerated"
    }
}
