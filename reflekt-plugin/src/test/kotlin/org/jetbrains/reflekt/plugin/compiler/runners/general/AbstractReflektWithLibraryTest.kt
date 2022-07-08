package org.jetbrains.reflekt.plugin.compiler.runners.general

import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.builders.configureIrHandlersStep
import org.jetbrains.kotlin.test.runners.codegen.commonHandlersForCodegenTest
import org.jetbrains.reflekt.plugin.compiler.handlers.ReflektImplDumpHandler
import org.jetbrains.reflekt.plugin.compiler.providers.reflekt.ReflektPluginWithLibraryProvider
import org.jetbrains.reflekt.plugin.compiler.runners.AbstractReflektRuntimeClassPathTest
import org.jetbrains.reflekt.plugin.compiler.runners.reflektRuntimeClassPath

open class AbstractReflektWithLibraryTest : AbstractReflektRuntimeClassPathTest() {
    override fun TestConfigurationBuilder.configuration() {
        reflektWithLibrary()
    }
}

fun TestConfigurationBuilder.reflektWithLibrary() {
    reflektRuntimeClassPath()

    commonHandlersForCodegenTest()

    configureIrHandlersStep {
        useHandlers(
            ::ReflektImplDumpHandler
        )
    }

    useConfigurators(
        ::ReflektPluginWithLibraryProvider,
    )
}
