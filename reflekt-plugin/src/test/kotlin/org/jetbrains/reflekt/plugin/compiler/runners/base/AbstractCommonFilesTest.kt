package org.jetbrains.reflekt.plugin.compiler.runners.base

import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.reflekt.plugin.compiler.providers.commonFiles.CommonFilesProvider
import org.jetbrains.reflekt.plugin.util.CodeGenTestPaths

/**
 * Tests with both helpers methods and common files provided.
 */
abstract class AbstractCommonFilesTest : AbstractHelpersTest() {
    override fun configure(builder: TestConfigurationBuilder) {
        super.configure(builder)
        builder.useAdditionalSourceProviders({ CommonFilesProvider(it, CodeGenTestPaths.additionalSourcesCommonFilesFolder) })
    }
}
