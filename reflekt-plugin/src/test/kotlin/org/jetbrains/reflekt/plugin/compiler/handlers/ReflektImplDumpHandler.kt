package org.jetbrains.reflekt.plugin.compiler.handlers

import org.jetbrains.kotlin.test.backend.handlers.AbstractIrHandler
import org.jetbrains.kotlin.test.backend.ir.IrBackendInput
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.TestServices
import org.jetbrains.kotlin.test.services.moduleStructure
import org.jetbrains.kotlin.test.services.temporaryDirectoryManager
import org.jetbrains.kotlin.test.utils.MultiModuleInfoDumper
import org.jetbrains.reflekt.plugin.compiler.directives.ReflektPluginCallDirectives.DUMP_REFLEKT_IMPL
import org.jetbrains.reflekt.plugin.compiler.providers.reflekt.ReflektPluginWithLibraryProvider
import java.io.File

class ReflektImplDumpHandler(testServices: TestServices) : AbstractIrHandler(testServices) {
    companion object {
        const val DUMP_EXTENSION = "expected.kt"
    }

    private val dumper = MultiModuleInfoDumper()

    override fun processAfterAllModules(someAssertionWasFailed: Boolean) {
        assertions.assertEqualsToFile(getExpectedFile(), dumper.generateResultingDump(), message = { "Content is not equal" })
    }

    private fun getExpectedFile(): File {
        val originalTestFolder = testServices.moduleStructure.originalTestDataFiles.first().parentFile
        return originalTestFolder.listFiles()?.find { it.name.endsWith(DUMP_EXTENSION) } ?: error("Can not find ReflektImpl expected file")
    }

    override fun processModule(module: TestModule, info: IrBackendInput) {
        if (DUMP_REFLEKT_IMPL !in module.directives) return
        val builder = dumper.builderForModule(module)
        val tmpDirectory = testServices.temporaryDirectoryManager.getOrCreateTempDirectory(ReflektPluginWithLibraryProvider.TMP_DIRECTORY_NAME)
        val actualFile = tmpDirectory.walkTopDown().find { it.name == "ReflektImpl.kt" } ?: error("Can not find ReflektImpl generated file")
        // TODO: think about comparing ReflektImpl files (different orders of found entities)
        builder.append(actualFile.readText())
    }
}
