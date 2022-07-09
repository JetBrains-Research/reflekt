package org.jetbrains.reflekt.plugin.compiler.handlers

import org.jetbrains.kotlin.test.backend.handlers.AbstractIrHandler
import org.jetbrains.kotlin.test.backend.ir.IrBackendInput
import org.jetbrains.kotlin.test.directives.model.SimpleDirective
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.*
import org.jetbrains.kotlin.test.utils.MultiModuleInfoDumper
import org.jetbrains.reflekt.plugin.compiler.providers.reflekt.ReflektPluginWithLibraryProvider
import java.io.File

abstract class ReflektImplBaseHandler(testServices: TestServices, private val serviceDirective: SimpleDirective) : AbstractIrHandler(testServices) {
    protected val dumper = MultiModuleInfoDumper()

    protected fun getTestRoot() = testServices.temporaryDirectoryManager.getOrCreateTempDirectory(ReflektPluginWithLibraryProvider.TMP_DIRECTORY_NAME)

    protected fun getReflektImplFile(testRoot: File = getTestRoot()) =
        testRoot.walkTopDown().find { it.name == "ReflektImpl.kt" } ?: error("Can not find ReflektImpl generated file")

    override fun processModule(module: TestModule, info: IrBackendInput) {
        if (serviceDirective !in module.directives) return
        val builder = dumper.builderForModule(module)
        val actualFile = getReflektImplFile()
        builder.append(actualFile.readText())
    }
}
