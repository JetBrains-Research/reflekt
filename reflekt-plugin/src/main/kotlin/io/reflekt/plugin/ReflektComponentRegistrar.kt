package io.reflekt.plugin

import com.google.auto.service.AutoService
import io.reflekt.util.FileUtil
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
        // TODO: I did not understand How can I get it from the gradle plugin????
        val filesToIntrospect = getKtFiles(getFilesToIntrospect(configuration[KEY_JAR_FILES]), project)
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

    private fun getFilesToIntrospect(jars: List<String>?): Set<File> {
        val filesToIntrospect: MutableSet<File> = HashSet()
        jars?.forEach {
            val file = File(it)
            if (file.isFile) {
                filesToIntrospect.addAll(FileUtil.extractAllFiles(file))
            }
        }
        return filesToIntrospect
    }

    private fun getProjectFiles() {
        TODO("Not implemented yet")
    }
}

