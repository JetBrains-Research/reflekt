package org.jetbrains.reflekt.plugin.compiler.runners

import org.jetbrains.kotlin.test.Constructor
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.services.AdditionalSourceProvider
import org.jetbrains.reflekt.plugin.compiler.providers.CommonFilesProvider

/**
 * Tests with both helpers methods and common files provided.
 */
open class AbstractCommonFilesTest : AbstractHelpersTest() {
    private val commonFilesProvider: Constructor<AdditionalSourceProvider> = { CommonFilesProvider(it, COMMON_FILES_PATH) }

    override fun configure(builder: TestConfigurationBuilder) {
        super.configure(builder)
        builder.useAdditionalSourceProviders(commonFilesProvider)
    }

    companion object {
        private const val COMMON_FILES_PATH: String = "reflekt-plugin/src/test/resources/org/jetbrains/reflekt/plugin/compiler/additional-sources/common-files"
    }
}





