package org.jetbrains.reflekt.plugin.compiler.providers.commonFiles

import org.jetbrains.kotlin.test.services.TestServices
import org.jetbrains.reflekt.plugin.util.CodeGenTestPaths

/**
 * Provides helper functions to call from 'box' tests such as Reflekt calls result parsing, etc.
 */
class HelperTestBoxProvider(
    testServices: TestServices,
    helpersPath: String = CodeGenTestPaths.additionalSourcesHelpersFolder,
) : CommonFilesProvider(testServices, helpersPath)
