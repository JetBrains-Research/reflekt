@file:Suppress("FILE_UNORDERED_IMPORTS")

package org.jetbrains.reflekt.plugin.generation.code.generator

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.reflekt.plugin.analysis.models.ir.LibraryQueriesResults
import org.jetbrains.reflekt.plugin.utils.Util.log
import java.io.File

class ReflektImplGeneratorExtension(
    private val libraryQueriesResults: LibraryQueriesResults,
    private val generationPath: File,
    private val messageCollector: MessageCollector? = null,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        messageCollector?.log("Start generation ReflektImpl. Base generation path: $generationPath")
        val reflektImplFile = File(generationPath, "io/reflekt/ReflektImpl.kt")
        messageCollector?.log("ReflektImpl generation path: ${reflektImplFile.absolutePath}")
        with(reflektImplFile) {
            delete()
            parentFile.mkdirs()
            writeText(
                ReflektImplGenerator(libraryQueriesResults).generate(),
            )
        }
        messageCollector?.log("Finish generation ReflektImpl")
    }
}
