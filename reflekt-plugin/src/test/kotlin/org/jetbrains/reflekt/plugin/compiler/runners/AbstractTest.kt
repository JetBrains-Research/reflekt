package org.jetbrains.reflekt.plugin.compiler.runners

import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.test.TargetBackend
import org.jetbrains.kotlin.test.backend.BlackBoxCodegenSuppressor
import org.jetbrains.kotlin.test.backend.ir.JvmIrBackendFacade
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.builders.configureClassicFrontendHandlersStep
import org.jetbrains.kotlin.test.directives.CodegenTestDirectives.DUMP_IR
import org.jetbrains.kotlin.test.directives.CodegenTestDirectives.IGNORE_DEXING
import org.jetbrains.kotlin.test.directives.ConfigurationDirectives.WITH_STDLIB
import org.jetbrains.kotlin.test.directives.JvmEnvironmentConfigurationDirectives.FULL_JDK
import org.jetbrains.kotlin.test.directives.JvmEnvironmentConfigurationDirectives.JVM_TARGET
import org.jetbrains.kotlin.test.directives.JvmEnvironmentConfigurationDirectives.WITH_REFLECT
import org.jetbrains.kotlin.test.frontend.classic.ClassicFrontend2IrConverter
import org.jetbrains.kotlin.test.frontend.classic.ClassicFrontendFacade
import org.jetbrains.kotlin.test.frontend.classic.handlers.DiagnosticMessagesTextHandler
import org.jetbrains.kotlin.test.model.DependencyKind
import org.jetbrains.kotlin.test.model.FrontendKinds
import org.jetbrains.kotlin.test.runners.RunnerWithTargetBackendForTestGeneratorMarker
import org.jetbrains.kotlin.test.runners.codegen.commonConfigurationForCodegenTest
import org.jetbrains.kotlin.test.runners.codegen.configureCommonHandlersForBoxTest
import org.jetbrains.reflekt.plugin.compiler.providers.reflekt.ReflektPluginProvider
import org.jetbrains.reflekt.plugin.compiler.providers.reflekt.ReflektRuntimeClasspathProvider

open class AbstractTest : BaseTestRunner(), RunnerWithTargetBackendForTestGeneratorMarker {
    override val targetBackend: TargetBackend
        get() = TargetBackend.JVM_IR


    override fun TestConfigurationBuilder.configuration() {
        commonConfigurationForCodegenTest(
            FrontendKinds.ClassicFrontend,
            ::ClassicFrontendFacade,
            ::ClassicFrontend2IrConverter,
            ::JvmIrBackendFacade
        )

        globalDefaults {
            targetBackend = TargetBackend.JVM_IR
            targetPlatform = JvmPlatforms.defaultJvmPlatform
            dependencyKind = DependencyKind.Binary
            frontend = FrontendKinds.ClassicFrontend
        }

        defaultDirectives {
            +DUMP_IR
            +WITH_STDLIB
            JVM_TARGET with JvmTarget.JVM_11
            +IGNORE_DEXING
            +FULL_JDK
            +WITH_REFLECT
        }

        configureClassicFrontendHandlersStep {
            useHandlers(
                ::DiagnosticMessagesTextHandler,
            )
        }

        configureCommonHandlersForBoxTest()
        useAfterAnalysisCheckers(::BlackBoxCodegenSuppressor)
        enableMetaInfoHandler()

        useConfigurators(
            ::ReflektPluginProvider,
        )
        useCustomRuntimeClasspathProviders(
            ::ReflektRuntimeClasspathProvider,
        )
    }
}

