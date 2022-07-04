package org.jetbrains.reflekt.plugin.compiler.runners

import org.jetbrains.kotlin.test.Constructor
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.services.AdditionalSourceProvider
import org.jetbrains.reflekt.plugin.compiler.providers.commonFiles.CommonFilesProvider

/**
 * Tests with both helpers methods and common files provided.
 */
open class AbstractCommonFilesTest : AbstractHelpersTest() {
    override fun configure(builder: TestConfigurationBuilder) {
        super.configure(builder)
        builder.useAdditionalSourceProviders({ CommonFilesProvider(it, COMMON_FILES_PATH) })
    }

    companion object {
        private const val COMMON_FILES_PATH: String = "reflekt-plugin/src/test/resources/org/jetbrains/reflekt/plugin/compiler/additional-sources/common-files"
    }
}
