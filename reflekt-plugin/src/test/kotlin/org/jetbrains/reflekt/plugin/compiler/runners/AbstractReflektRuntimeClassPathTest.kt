package org.jetbrains.reflekt.plugin.compiler.runners

import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.reflekt.plugin.compiler.providers.reflekt.ReflektRuntimeClasspathProvider
import org.jetbrains.reflekt.plugin.compiler.runners.base.AbstractCommonFilesTest
import org.jetbrains.reflekt.plugin.compiler.runners.base.baseOldFrontEndIrBackEndBoxConfiguration

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
