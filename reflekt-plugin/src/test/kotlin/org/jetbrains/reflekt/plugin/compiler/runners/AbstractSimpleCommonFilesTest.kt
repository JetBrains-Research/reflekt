package org.jetbrains.reflekt.plugin.compiler.runners

import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.reflekt.plugin.compiler.providers.*

open class AbstractSimpleCommonFileTest : AbstractTest() {
    override fun configure(builder: TestConfigurationBuilder) {
        super.configure(builder)
        builder.useAdditionalSourceProviders( { HelperTestBoxProvider(it) } )
//        builder.useAdditionalSourceProviders(::SimpleCommonFileProvider)
    }
}
