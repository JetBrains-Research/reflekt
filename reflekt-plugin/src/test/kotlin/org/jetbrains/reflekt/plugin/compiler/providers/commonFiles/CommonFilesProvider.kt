package org.jetbrains.reflekt.plugin.compiler.providers.commonFiles

import org.jetbrains.kotlin.test.directives.model.RegisteredDirectives
import org.jetbrains.kotlin.test.model.TestFile
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.AdditionalSourceProvider
import org.jetbrains.kotlin.test.services.TestServices
import java.io.File

/**
 * Provides the sources from the whole [filesFolder].
 */
open class CommonFilesProvider(testServices: TestServices, private val filesFolder: String) : AdditionalSourceProvider(testServices) {

    override fun produceAdditionalFiles(globalDirectives: RegisteredDirectives, module: TestModule): List<TestFile> {
        val commonFiles = File(filesFolder).walkTopDown().filter { it.isFile }.toList()
        return commonFiles.map { it.toTestFile() }
    }
}
