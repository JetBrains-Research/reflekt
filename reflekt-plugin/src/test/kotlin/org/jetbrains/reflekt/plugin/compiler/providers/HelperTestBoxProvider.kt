package org.jetbrains.reflekt.plugin.compiler.providers

import org.jetbrains.kotlin.test.directives.model.RegisteredDirectives
import org.jetbrains.kotlin.test.model.TestFile
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.AdditionalSourceProvider
import org.jetbrains.kotlin.test.services.TestServices
import java.io.File

/**
 * Provides helper functions to call from 'box' tests such as Reflekt calls result parsing, etc.
 */
class HelperTestBoxProvider(
    testServices: TestServices,
    helpersPath: String = "reflekt-plugin/src/test/resources/org/jetbrains/reflekt/plugin/compiler/additional-sources/helpers",
) : CommonFilesProvider(testServices, helpersPath)



