package org.jetbrains.reflekt.plugin.compiler.runners

import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.reflekt.plugin.compiler.providers.reflekt.ReflektRuntimeClasspathProvider

abstract class AbstractReflektRuntimeClassPathTest: AbstractCommonFilesTest() {
    override fun TestConfigurationBuilder.configuration() {
        reflektRuntimeClassPath()
    }
}

fun TestConfigurationBuilder.reflektRuntimeClassPath() {
    baseOldFrontEndIrBackEndBoxConfiguration()

    useCustomRuntimeClasspathProviders(
        ::ReflektRuntimeClasspathProvider,
    )
}
