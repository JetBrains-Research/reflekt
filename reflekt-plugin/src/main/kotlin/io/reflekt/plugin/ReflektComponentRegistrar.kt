package io.reflekt.plugin

import com.google.auto.service.AutoService
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
    override fun registerProjectComponents(
        project: MockProject,
        configuration: CompilerConfiguration
    ) {
        if (configuration[KEY_ENABLED] == false) {
            return
        }
        val filesToIntrospect = getKtFiles(getFilesToIntrospect(configuration[KEY_INTROSPECT_FILES]), project)
        AnalysisHandlerExtension.registerExtension(
            project,
            ReflektAnalysisExtension(filesToIntrospect = filesToIntrospect)
        )
        // TODO: update code
    }

    /** Get KtFile representation for set of files */
    private fun getKtFiles(files: Collection<File>, project: MockProject): Set<KtFile> {
        return files.mapNotNull { file ->
            PsiFileFactory.getInstance(project).createFileFromText(KotlinLanguage.INSTANCE, file.readText()) as? KtFile
        }.toSet()
    }

    private fun getFilesToIntrospect(files: List<String>?): Set<File> {
        return files?.map { File(it) }?.filter { it.isFile }?.toSet() ?: emptySet()
    }
}

