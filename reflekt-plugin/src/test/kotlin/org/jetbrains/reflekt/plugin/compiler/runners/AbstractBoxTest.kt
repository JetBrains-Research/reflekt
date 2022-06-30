package org.jetbrains.reflekt.plugin.compiler.runners

import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.test.TargetBackend
import org.jetbrains.kotlin.test.backend.BlackBoxCodegenSuppressor
import org.jetbrains.kotlin.test.backend.handlers.IrTextDumpHandler
import org.jetbrains.kotlin.test.backend.handlers.IrTreeVerifierHandler
import org.jetbrains.kotlin.test.backend.handlers.JvmBoxRunner
import org.jetbrains.kotlin.test.backend.ir.JvmIrBackendFacade
import org.jetbrains.kotlin.test.builders.*
import org.jetbrains.kotlin.test.directives.CodegenTestDirectives.DUMP_IR
import org.jetbrains.kotlin.test.directives.CodegenTestDirectives.IGNORE_DEXING
import org.jetbrains.kotlin.test.directives.ConfigurationDirectives.WITH_STDLIB
import org.jetbrains.kotlin.test.directives.JvmEnvironmentConfigurationDirectives
import org.jetbrains.kotlin.test.directives.JvmEnvironmentConfigurationDirectives.FULL_JDK
import org.jetbrains.kotlin.test.directives.JvmEnvironmentConfigurationDirectives.JVM_TARGET
import org.jetbrains.kotlin.test.directives.model.RegisteredDirectives
import org.jetbrains.kotlin.test.frontend.classic.ClassicFrontend2IrConverter
import org.jetbrains.kotlin.test.frontend.classic.ClassicFrontendFacade
import org.jetbrains.kotlin.test.frontend.classic.handlers.*
import org.jetbrains.kotlin.test.model.*
import org.jetbrains.kotlin.test.runners.*
import org.jetbrains.kotlin.test.runners.codegen.commonConfigurationForCodegenTest
import org.jetbrains.kotlin.test.runners.codegen.configureCommonHandlersForBoxTest
import org.jetbrains.kotlin.test.services.AdditionalSourceProvider
import org.jetbrains.kotlin.test.services.TestServices
import org.jetbrains.kotlin.test.services.configuration.CommonEnvironmentConfigurator
import org.jetbrains.kotlin.test.services.configuration.JvmEnvironmentConfigurator
import org.jetbrains.reflekt.plugin.compiler.providers.ReflektPluginProvider
import org.jetbrains.reflekt.plugin.compiler.providers.ReflektRuntimeClasspathProvider
import java.io.File

// просто тесты, тесты с общими файлами, тесты с рефлектом,
open class AbstractBoxTest : BaseTestRunner(), RunnerWithTargetBackendForTestGeneratorMarker {
    override val targetBackend: TargetBackend
        get() = TargetBackend.JVM_IR


    override fun TestConfigurationBuilder.configuration() {
        // todo: move out a separate version of black box with common files?
        useAdditionalSourceProviders(::CommonFilesProvider)

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
            ::ReflektRuntimeClasspathProvider
        )
    }
}

class CommonFilesProvider(testServices: TestServices) : AdditionalSourceProvider(testServices) {
    override fun produceAdditionalFiles(globalDirectives: RegisteredDirectives, module: TestModule): List<TestFile> {
        return listOf(File("reflekt-plugin/src/test/resources/org/jetbrains/reflekt/plugin/compiler/commonFiles/Annotations.kt").toTestFile())
    }
}
