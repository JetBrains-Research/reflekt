package io.reflekt.plugin

import io.reflekt.plugin.analysis.ReflektAnalyzer
import io.reflekt.plugin.utils.Util.log
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisHandlerExtension

class ReflektAnalysisExtension(private val filesToIntrospect: Set<KtFile>,
                               private val messageCollector: MessageCollector? = null) : AnalysisHandlerExtension {

    override fun analysisCompleted(project: Project, module: ModuleDescriptor, bindingTrace: BindingTrace, files: Collection<KtFile>): AnalysisResult? {
        messageCollector?.log("ReflektAnalysisExtension is starting...")
        messageCollector?.log("FILES: ${files.joinToString(separator = ", ") { it.name }};")
        val analyzer = ReflektAnalyzer(files.toSet().union(filesToIntrospect), bindingTrace.bindingContext)
        val invokes = analyzer.invokes()
        messageCollector?.log("INVOKES: $invokes;")
        val uses = analyzer.uses(invokes)
        messageCollector?.log("USES: $uses;")
        // TODO: add uses into bindingContext???

        return super.analysisCompleted(project, module, bindingTrace, files)
    }

}
