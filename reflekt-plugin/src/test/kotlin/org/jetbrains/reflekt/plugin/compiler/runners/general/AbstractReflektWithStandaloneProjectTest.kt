package org.jetbrains.reflekt.plugin.compiler.runners.general

import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.runners.codegen.configureCommonHandlersForBoxTest
import org.jetbrains.reflekt.plugin.compiler.providers.reflekt.ReflektPluginWithStandaloneProjectProvider
import org.jetbrains.reflekt.plugin.compiler.runners.AbstractReflektRuntimeClassPathTest
import org.jetbrains.reflekt.plugin.compiler.runners.reflektRuntimeClassPath

open class AbstractReflektWithStandaloneProjectTest : AbstractReflektRuntimeClassPathTest() {
    override fun TestConfigurationBuilder.configuration() {
        reflektWithStandaloneProject()
    }
}

fun TestConfigurationBuilder.reflektWithStandaloneProject() {
    reflektRuntimeClassPath()

    configureCommonHandlersForBoxTest()

    useConfigurators(
        ::ReflektPluginWithStandaloneProjectProvider,
    )
}
