package org.jetbrains.reflekt.plugin.analysis

import org.jetbrains.reflekt.plugin.analysis.models.*
import org.jetbrains.reflekt.plugin.generation.code.generator.ReflektImplGenerator
import org.jetbrains.reflekt.plugin.utils.Util.getInstances
import org.jetbrains.reflekt.plugin.utils.Util.getUses
import org.jetbrains.reflekt.plugin.utils.Util.log
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisHandlerExtension
import java.io.File

class ReflektModuleAnalysisExtension(private val filesToIntrospect: Set<KtFile>,
                                     private val generationPath: File?,
                                     private val reflektContext: ReflektContext? = null,
                                     private val messageCollector: MessageCollector? = null) : AnalysisHandlerExtension {

    override fun analysisCompleted(project: Project, module: ModuleDescriptor, bindingTrace: BindingTrace, files: Collection<KtFile>): AnalysisResult? {
        messageCollector?.log("ReflektAnalysisExtension is starting...")
        messageCollector?.log("FILES: ${files.joinToString(separator = ", ") { it.name }};")
        messageCollector?.log("Start analysis ${module.name} module's files;")
        val allFiles = files.toSet().union(filesToIntrospect)
        val uses = getUses(allFiles, bindingTrace)
        val instances = getInstances(allFiles, bindingTrace)
        if (reflektContext != null) {
            reflektContext.uses = IrReflektUses.fromReflektUses(uses, bindingTrace.bindingContext)
            reflektContext.instances = IrReflektInstances.fromReflektInstances(instances, bindingTrace.bindingContext)
            messageCollector?.log("Finish analysis ${module.name} module's files;\nUses: ${reflektContext.uses}\nInstances: ${reflektContext.instances}")
        } else {
            messageCollector?.log("Finish analysis ${module.name} module's files;\nUses: $uses\nInstances: $instances")
        }

        if (generationPath != null) {
            with(File(generationPath, "org/jetbrains/reflekt/ReflektImpl.kt")) {
                delete()
                parentFile.mkdirs()
                writeText(
                    ReflektImplGenerator(uses).generate()
                )
            }
        }

        return super.analysisCompleted(project, module, bindingTrace, files)
    }
}
