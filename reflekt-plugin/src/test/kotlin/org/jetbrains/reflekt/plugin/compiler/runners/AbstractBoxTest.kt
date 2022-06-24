package org.jetbrains.reflekt.plugin.compiler.runners

import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.test.TargetBackend
import org.jetbrains.kotlin.test.backend.BlackBoxCodegenSuppressor
import org.jetbrains.kotlin.test.backend.handlers.IrTextDumpHandler
import org.jetbrains.kotlin.test.backend.handlers.IrTreeVerifierHandler
import org.jetbrains.kotlin.test.backend.handlers.JvmBoxRunner
import org.jetbrains.kotlin.test.backend.ir.JvmIrBackendFacade
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.builders.irHandlersStep
import org.jetbrains.kotlin.test.builders.jvmArtifactsHandlersStep
import org.jetbrains.kotlin.test.directives.CodegenTestDirectives.DUMP_IR
import org.jetbrains.kotlin.test.model.DependencyKind
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.runners.RunnerWithTargetBackendForTestGeneratorMarker
import org.jetbrains.kotlin.test.services.EnvironmentConfigurator
import org.jetbrains.kotlin.test.services.TestServices
import org.jetbrains.reflekt.plugin.analysis.analyzer.IrInstancesAnalyzer
import org.jetbrains.reflekt.plugin.analysis.collector.ir.InstancesCollectorExtension
import org.jetbrains.reflekt.plugin.analysis.models.ir.LibraryArgumentsWithInstances
import org.jetbrains.reflekt.plugin.compiler.providers.ReflektPluginProvider

open class AbstractBoxTest : BaseTestRunner(), RunnerWithTargetBackendForTestGeneratorMarker {
    override val targetBackend: TargetBackend
        get() = TargetBackend.JVM_IR

    override fun TestConfigurationBuilder.configuration() {
        globalDefaults {
            targetBackend = TargetBackend.JVM_IR
            targetPlatform = JvmPlatforms.defaultJvmPlatform
            dependencyKind = DependencyKind.Binary
        }

        defaultDirectives {
            +DUMP_IR
        }

        irHandlersStep {
            useHandlers(
                ::IrTextDumpHandler,
                ::IrTreeVerifierHandler,
            )
        }
        facadeStep(::JvmIrBackendFacade)
        jvmArtifactsHandlersStep {
            useHandlers(::JvmBoxRunner)
        }

        useAfterAnalysisCheckers(::BlackBoxCodegenSuppressor)

        useConfigurators(
            ::ReflektPluginProvider,
        )

//        useConfigurators(
//            ::ExtensionRegistrarConfigurator
//        )
    }
}

//class ExtensionRegistrarConfigurator(testServices: TestServices) : EnvironmentConfigurator(testServices) {
//    val instancesAnalyzer = IrInstancesAnalyzer()
//    val libraryArgumentsWithInstances = LibraryArgumentsWithInstances()
//
////    override fun registerCompilerExtensions(project: Project, module: TestModule, configuration: CompilerConfiguration) {
////        IrGenerationExtension.registerExtension(project, InstancesCollectorExtension(
////            irInstancesAnalyzer = instancesAnalyzer,
////            reflektMetaFilesFromLibraries = emptySet(),
////            libraryArgumentsWithInstances = libraryArgumentsWithInstances,
////        ))
////    }
//}
