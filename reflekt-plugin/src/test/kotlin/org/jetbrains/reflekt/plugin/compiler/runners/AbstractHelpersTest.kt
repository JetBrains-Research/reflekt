package org.jetbrains.reflekt.plugin.compiler.runners

import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.reflekt.plugin.compiler.providers.commonFiles.HelperTestBoxProvider

/**
 * Tests with helper methods added as an aditional source, so they can be used from the box tests.
 */
open class AbstractHelpersTest : AbstractTest() {
    override fun configure(builder: TestConfigurationBuilder) {
        super.configure(builder)
        builder.useAdditionalSourceProviders({ HelperTestBoxProvider(it) })
    }
}
