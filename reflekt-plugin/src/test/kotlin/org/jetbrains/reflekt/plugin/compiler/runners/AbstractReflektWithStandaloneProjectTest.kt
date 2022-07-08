package org.jetbrains.reflekt.plugin.compiler.runners

import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.reflekt.plugin.compiler.providers.reflekt.ReflektPluginWithStandaloneProjectProvider

open class AbstractReflektWithStandaloneProjectTest : AbstractReflektRuntimeClassPathTest() {
    override fun TestConfigurationBuilder.configuration() {
        reflektWithStandaloneProject()
    }
}

fun TestConfigurationBuilder.reflektWithStandaloneProject() {
    reflektRuntimeClassPath()

    useConfigurators(
        ::ReflektPluginWithStandaloneProjectProvider,
    )
}
