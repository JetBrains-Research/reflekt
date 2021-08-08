package io.reflekt.plugin

import com.google.auto.service.AutoService
import io.reflekt.plugin.analysis.ReflektModuleAnalysisExtension
import io.reflekt.plugin.analysis.models.ReflektContext
import io.reflekt.plugin.generation.ir.ReflektIrGenerationExtension
import io.reflekt.plugin.generation.ir.SmartReflektIrGenerationExtension
import io.reflekt.plugin.utils.Keys
import io.reflekt.plugin.utils.Util.initMessageCollector
import io.reflekt.plugin.utils.Util.log
import io.reflekt.plugin.utils.Util.messageCollector
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.com.intellij.psi.PsiFileFactory
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisHandlerExtension
import java.io.File

@AutoService(ComponentRegistrar::class)
class ReflektComponentRegistrar(private val hasConfiguration: Boolean = true) : ComponentRegistrar {
//     The path will be: pathToKotlin/daemon/reflekt-log.log
    private val logFilePath = "reflekt-log.log"

    override fun registerProjectComponents(
        project: MockProject,
        configuration: CompilerConfiguration
    ) {
        if (hasConfiguration && configuration[Keys.ENABLED] != true) {
            return
        }
        configuration.initMessageCollector(logFilePath)
        if (configuration[Keys.INTROSPECT_FILES] != null && configuration[Keys.OUTPUT_DIR] == null) {
            error("Output path not specified")
        }
        val dependencyJars = configuration[Keys.DEPENDENCY_JARS] ?: emptyList()
        configuration.messageCollector.log("DEPENDENCY JARS: ${dependencyJars.map { it.absolutePath }};")

        val filesToIntrospect = getKtFiles(configuration[Keys.INTROSPECT_FILES] ?: emptyList(), project)
        val outputDir = configuration[Keys.OUTPUT_DIR]
        val reflektContext = ReflektContext()

        // Todo: will this be called multiple times (for each project module)? can we avoid this?
        AnalysisHandlerExtension.registerExtension(
            project,
            ReflektModuleAnalysisExtension(
                filesToIntrospect = filesToIntrospect,
                generationPath = outputDir,
                reflektContext = reflektContext,
                messageCollector = configuration.messageCollector
            )
        )
        IrGenerationExtension.registerExtension(
            project,
            ReflektIrGenerationExtension(
                reflektContext = reflektContext,
                messageCollector = configuration.messageCollector
            )
        )
        IrGenerationExtension.registerExtension(
            project,
            SmartReflektIrGenerationExtension(
                classpath = dependencyJars,
                reflektContext = reflektContext,
                messageCollector = configuration.messageCollector
            )
        )
    }

    /** Get KtFile representation for set of files */
    private fun getKtFiles(files: Collection<File>, project: MockProject): Set<KtFile> {
        return files.filter {  it.extension == "kt" }.mapNotNull { file ->
            PsiFileFactory.getInstance(project).createFileFromText(KotlinLanguage.INSTANCE, file.readText()) as? KtFile
        }.toSet()
    }
}

