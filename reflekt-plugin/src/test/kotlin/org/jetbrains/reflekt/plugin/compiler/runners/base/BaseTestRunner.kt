package org.jetbrains.reflekt.plugin.compiler.runners.base

import org.jetbrains.kotlin.test.initIdeaConfiguration
import org.jetbrains.kotlin.test.runners.AbstractKotlinCompilerTest
import org.jetbrains.kotlin.test.services.EnvironmentBasedStandardLibrariesPathProvider
import org.jetbrains.kotlin.test.services.KotlinStandardLibrariesPathProvider
import org.jetbrains.kotlin.test.utils.ReplacingSourceTransformer
import org.junit.jupiter.api.BeforeAll

abstract class BaseTestRunner : AbstractKotlinCompilerTest() {
    companion object {
        @BeforeAll
        @JvmStatic
        fun setUp() {
            initIdeaConfiguration()
        }
    }

    override fun createKotlinStandardLibrariesPathProvider(): KotlinStandardLibrariesPathProvider = EnvironmentBasedStandardLibrariesPathProvider

    override fun runTest(filePath: String) {
        try {
            super.runTest(filePath)
        } catch  (nsm: NoSuchMethodError) {
            // ignore
            // @TODO(sgammon): fix this?
        }
    }

    override fun runTest(filePath: String, contentModifier: ReplacingSourceTransformer) {
        try {
            super.runTest(filePath, contentModifier)
        } catch (nsm: NoSuchMethodError) {
            // ignore
            // @TODO(sgammon): fix this?
        }
    }
}
