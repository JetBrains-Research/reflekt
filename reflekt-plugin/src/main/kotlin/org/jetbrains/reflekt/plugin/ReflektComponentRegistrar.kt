package org.jetbrains.reflekt.plugin

import com.google.auto.service.AutoService
import org.jetbrains.reflekt.plugin.analysis.ReflektModuleAnalysisExtension
import org.jetbrains.reflekt.plugin.analysis.models.ReflektContext
import org.jetbrains.reflekt.plugin.generation.ir.ReflektIrGenerationExtension
import org.jetbrains.reflekt.plugin.generation.ir.SmartReflektIrGenerationExtension
import org.jetbrains.reflekt.plugin.utils.Keys
import org.jetbrains.reflekt.plugin.utils.Util.initMessageCollector
import org.jetbrains.reflekt.plugin.utils.Util.log
import org.jetbrains.reflekt.plugin.utils.Util.messageCollector
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.com.intellij.psi.PsiFileFactory
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys.INCREMENTAL_COMPILATION_COMPONENTS
import org.jetbrains.kotlin.config.JVMConfigurationKeys.OUTPUT_DIRECTORY
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisHandlerExtension
import java.io.File

@AutoService(ComponentRegistrar::class)
class ReflektComponentRegistrar(private val hasConfiguration: Boolean = true) : ComponentRegistrar {
    // The path will be: pathToKotlin/daemon/reflekt-log.log
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
        configuration.messageCollector.log("CompilerConfiguration out dir: ${configuration.get(OUTPUT_DIRECTORY)?.path}")
        configuration.messageCollector.log("CompilerConfiguration incremental cache provider: ${configuration.get(INCREMENTAL_COMPILATION_COMPONENTS)}")

        // This will be called multiple times (for each project module),
        // since compilation process runs module by module
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

