package io.reflekt.plugin.analysis

import io.reflekt.plugin.utils.Util.getUses
import io.reflekt.plugin.utils.Util.log
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisHandlerExtension

class ReflektModuleAnalysisExtension(private val messageCollector: MessageCollector? = null) : AnalysisHandlerExtension {

    override fun analysisCompleted(project: Project, module: ModuleDescriptor, bindingTrace: BindingTrace, files: Collection<KtFile>): AnalysisResult? {
        messageCollector?.log("ReflektAnalysisExtension is starting...")
        messageCollector?.log("FILES: ${files.joinToString(separator = ", ") { it.name }};")
        messageCollector?.log("Start analysis ${module.name} module's files;")
        val uses = getUses(files.toSet(), bindingTrace)
        messageCollector?.log("Finish analysis ${module.name} module's files;\n Uses: $uses")
        return super.analysisCompleted(project, module, bindingTrace, files)
    }
}
