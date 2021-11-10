package io.reflekt.plugin.analysis

import io.reflekt.plugin.analysis.models.*
import io.reflekt.plugin.generation.code.generator.ReflektImplGenerator
import io.reflekt.plugin.utils.Util.getInstances
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

class ReflektModuleAnalysisExtension(private val filesToIntrospect: Set<KtFile>,
                                     private val generationPath: File?,
                                     private val reflektContext: ReflektContext? = null,
                                     private val messageCollector: MessageCollector? = null) : AnalysisHandlerExtension {
    override fun analysisCompleted(
        project: Project,
        module: ModuleDescriptor,
        bindingTrace: BindingTrace,
        files: Collection<KtFile>): AnalysisResult? {
        messageCollector?.log("ReflektAnalysisExtension is starting...")
        messageCollector?.log("FILES: ${files.joinToString(separator = ", ") { it.name }};")
        messageCollector?.log("Start analysis ${module.name} module's files;")
        val allFiles = files.toSet().union(filesToIntrospect)
        val uses = getUses(allFiles, bindingTrace)
        val instances = getInstances(allFiles, bindingTrace)
        reflektContext?.let {
            reflektContext.uses = IrReflektUses.fromReflektUses(uses, bindingTrace.bindingContext)
            reflektContext.instances = IrReflektInstances.fromReflektInstances(instances, bindingTrace.bindingContext)
            messageCollector?.log("Finish analysis ${module.name} module's files;\nUses: ${reflektContext.uses}\nInstances: ${reflektContext.instances}")
        } ?: run {
            messageCollector?.log("Finish analysis ${module.name} module's files;\nUses: $uses\nInstances: $instances")
        }

        generationPath?.let {
            with(File(generationPath, "io/reflekt/ReflektImpl.kt")) {
                delete()
                parentFile.mkdirs()
                writeText(
                    ReflektImplGenerator(uses).generate(),
                )
            }
        }

        return super.analysisCompleted(project, module, bindingTrace, files)
    }
}
