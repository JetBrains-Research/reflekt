package io.reflekt.plugin.generation.code

import io.reflekt.plugin.generation.code.generator.ReflektImplGenerator
import io.reflekt.plugin.utils.Util.getUses
import io.reflekt.plugin.utils.Util.log
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisHandlerExtension
import java.io.File

class ReflektCodeGeneratorExtension(private val filesToIntrospect: Set<KtFile>,
                                    private val generationPath: File,
                                    private val messageCollector: MessageCollector? = null) : AnalysisHandlerExtension {

    override fun analysisCompleted(project: Project, module: ModuleDescriptor, bindingTrace: BindingTrace, files: Collection<KtFile>): AnalysisResult? {
        messageCollector?.log("ReflektCodeGeneratorExtension is starting...")
        val uses = getUses(filesToIntrospect, bindingTrace, false)
        messageCollector?.log("Finish analysis ${module.name} module's files;\n Uses for external libraries: $uses")

        with(File(generationPath, "io/reflekt/ReflektImpl.kt")) {
            delete()
            parentFile.mkdirs()
            writeText(
                ReflektImplGenerator(uses).generate()
            )
        }

        return super.analysisCompleted(project, module, bindingTrace, files)
    }
}
