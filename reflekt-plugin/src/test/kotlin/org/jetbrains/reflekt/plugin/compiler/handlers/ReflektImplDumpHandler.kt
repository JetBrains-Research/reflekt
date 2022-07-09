package org.jetbrains.reflekt.plugin.compiler.handlers

import org.jetbrains.kotlin.test.services.TestServices
import org.jetbrains.kotlin.test.services.moduleStructure
import org.jetbrains.reflekt.plugin.compiler.directives.ReflektGeneralCallDirectives.DUMP_REFLEKT_IMPL
import java.io.File

class ReflektImplDumpHandler(testServices: TestServices) : ReflektImplBaseHandler(testServices, DUMP_REFLEKT_IMPL) {
    companion object {
        const val DUMP_EXTENSION = "expected.kt"
    }

    override fun processAfterAllModules(someAssertionWasFailed: Boolean) {
        // TODO: think about comparing ReflektImpl files (different orders of found entities)
        assertions.assertEqualsToFile(getExpectedFile(), dumper.generateResultingDump(), message = { "Content is not equal" })
    }

    private fun getExpectedFile(): File {
        val originalTestFolder = testServices.moduleStructure.originalTestDataFiles.first().parentFile
        return originalTestFolder.listFiles()?.find { it.name.endsWith(DUMP_EXTENSION) } ?: error("Can not find ReflektImpl expected file")
    }
}
