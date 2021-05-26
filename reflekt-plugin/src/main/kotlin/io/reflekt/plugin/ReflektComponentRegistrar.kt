package io.reflekt.plugin

import com.google.auto.service.AutoService
import io.reflekt.plugin.generation.ir.ReflektIrGenerationExtension
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
import java.io.File

@AutoService(ComponentRegistrar::class)
class ReflektComponentRegistrar : ComponentRegistrar {
    // The path will be: pathToKotlin/daemon/reflekt-log.log
    private val logFilePath = "reflekt-log.log"

    override fun registerProjectComponents(
        project: MockProject,
        configuration: CompilerConfiguration
    ) {
        if (configuration[Keys.ENABLED] != true) {
            return
        }
        configuration.initMessageCollector(logFilePath)
        if (configuration[Keys.INTROSPECT_FILES] != null && configuration[Keys.OUTPUT_DIR] == null) {
            error("Output path not specified")
        }
        val dependencyJars = configuration[Keys.DEPENDENCY_JARS] ?: emptyList()
        configuration.messageCollector.log("DEPENDENCY JARS: ${dependencyJars.map { it.absolutePath }};")

        //val filesToIntrospect = getKtFiles(configuration[Keys.INTROSPECT_FILES] ?: emptyList(), project)
        //val outputDir = configuration[Keys.OUTPUT_DIR] ?: File("")
        // Todo: will this be called multiple times (for each project module)? can we avoid this?
        IrGenerationExtension.registerExtension(
            project,
            ReflektIrGenerationExtension(
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

