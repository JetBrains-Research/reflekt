package org.jetbrains.reflekt.plugin.compiler.runners

import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.test.TargetBackend
import org.jetbrains.kotlin.test.backend.BlackBoxCodegenSuppressor
import org.jetbrains.kotlin.test.backend.handlers.IrTextDumpHandler
import org.jetbrains.kotlin.test.backend.handlers.IrTreeVerifierHandler
import org.jetbrains.kotlin.test.backend.handlers.JvmBoxRunner
import org.jetbrains.kotlin.test.backend.ir.JvmIrBackendFacade
import org.jetbrains.kotlin.test.builders.*
import org.jetbrains.kotlin.test.directives.CodegenTestDirectives.DUMP_IR
import org.jetbrains.kotlin.test.directives.ConfigurationDirectives.WITH_STDLIB
import org.jetbrains.kotlin.test.frontend.classic.handlers.ClassicDiagnosticsHandler
import org.jetbrains.kotlin.test.frontend.classic.handlers.DiagnosticMessagesTextHandler
import org.jetbrains.kotlin.test.model.DependencyKind
import org.jetbrains.kotlin.test.model.FrontendKinds
import org.jetbrains.kotlin.test.runners.*
import org.jetbrains.reflekt.plugin.compiler.providers.ReflektPluginProvider
import org.jetbrains.reflekt.plugin.compiler.providers.ReflektRuntimeClasspathProvider

open class AbstractBoxTest : BaseTestRunner(), RunnerWithTargetBackendForTestGeneratorMarker {
    override val targetBackend: TargetBackend
        get() = TargetBackend.JVM_IR

    override fun TestConfigurationBuilder.configuration() {
        globalDefaults {
            targetBackend = TargetBackend.JVM_IR
            targetPlatform = JvmPlatforms.defaultJvmPlatform
            dependencyKind = DependencyKind.Binary
            frontend = FrontendKinds.ClassicFrontend
        }

        defaultDirectives {
            +DUMP_IR
            +WITH_STDLIB
        }

        classicFrontendStep()
        classicFrontendHandlersStep {
            useHandlers(
                ::ClassicDiagnosticsHandler,
                ::DiagnosticMessagesTextHandler
            )
        }

        psi2IrStep()

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
        enableMetaInfoHandler()

        useConfigurators(
            ::ReflektPluginProvider,
        )
        useCustomRuntimeClasspathProviders(
            ::ReflektRuntimeClasspathProvider
        )
    }
}
