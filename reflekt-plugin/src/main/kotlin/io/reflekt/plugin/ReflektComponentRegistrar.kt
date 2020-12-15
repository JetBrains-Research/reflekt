package io.reflekt.plugin

import com.google.auto.service.AutoService
import io.reflekt.plugin.analysis.ReflektModuleAnalysisExtension
import io.reflekt.plugin.generation.bytecode.ReflektGeneratorExtension
import io.reflekt.plugin.utils.Keys
import io.reflekt.plugin.utils.Util.initMessageCollector
import io.reflekt.plugin.utils.Util.messageCollector
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.com.intellij.psi.PsiFileFactory
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisHandlerExtension
import java.io.File

@AutoService(ComponentRegistrar::class)
class ReflektComponentRegistrar : ComponentRegistrar {
    // The path will be: pathToKotlin/daemon/reflekt-log.log
    private val logFilePath = "reflekt-log.log"

    override fun registerProjectComponents(
        project: MockProject,
        configuration: CompilerConfiguration
    ) {
        if (configuration[Keys.ENABLED] == false) {
            return
        }
        configuration.initMessageCollector(logFilePath)
        val filesToIntrospect = getKtFiles(configuration[Keys.INTROSPECT_FILES] ?: emptyList(), project)
        val outputDir = configuration[Keys.OUTPUT_DIR] ?: error("Output path not specified")
        // Todo: will this be called multiple times (for each ptoject module)? can we avoid this?
        AnalysisHandlerExtension.registerExtension(
            project,
            ReflektModuleAnalysisExtension(
                filesToIntrospect = filesToIntrospect,
                generationPath = outputDir,
                messageCollector = configuration.messageCollector
            )
        )
        ExpressionCodegenExtension.registerExtension(
            project,
            ReflektGeneratorExtension(messageCollector = configuration.messageCollector)
        )
    }

    /** Get KtFile representation for set of files */
    private fun getKtFiles(files: Collection<File>, project: MockProject): Set<KtFile> {
        return files.mapNotNull { file ->
            PsiFileFactory.getInstance(project).createFileFromText(KotlinLanguage.INSTANCE, file.readText()) as? KtFile
        }.toSet()
    }
}

