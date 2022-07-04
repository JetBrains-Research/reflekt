package org.jetbrains.reflekt.plugin.compiler.providers.commonFiles

import org.jetbrains.kotlin.test.services.TestServices

/**
 * Provides helper functions to call from 'box' tests such as Reflekt calls result parsing, etc.
 */
class HelperTestBoxProvider(
    testServices: TestServices,
    helpersPath: String = "reflekt-plugin/src/test/resources/org/jetbrains/reflekt/plugin/compiler/additional-sources/helpers",
) : CommonFilesProvider(testServices, helpersPath)



