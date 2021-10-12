package io.reflekt.plugin.analysis

import io.reflekt.plugin.analysis.analyzer.descriptor.DescriptorAnalyzer
import io.reflekt.plugin.analysis.analyzer.source.ReflektAnalyzer
import io.reflekt.plugin.analysis.models.*
import io.reflekt.plugin.analysis.resolve.*
import io.reflekt.plugin.analysis.serialization.SerializationUtils
import io.reflekt.plugin.generation.code.generator.ReflektImplGenerator
import io.reflekt.plugin.utils.Util.getInstances
import io.reflekt.plugin.utils.Util.log
import io.reflekt.plugin.utils.Util.saveUses
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisHandlerExtension
import java.io.File

class ReflektModuleAnalysisExtension(
    private val reflektMetaFiles: Set<File>,
    private val toSaveMetadata: Boolean,
    private val generationPath: File?,
    private val reflektMetaFile: File,
    private val librariesToIntrospect: Set<String>,
    private val reflektContext: ReflektContext? = null,
    private val messageCollector: MessageCollector? = null
) : AnalysisHandlerExtension {

    override fun analysisCompleted(project: Project, module: ModuleDescriptor, bindingTrace: BindingTrace, files: Collection<KtFile>): AnalysisResult? {
        messageCollector?.log("ReflektAnalysisExtension is starting...")
        // TODO:
        reflektMetaFiles.forEach {
            messageCollector?.log("deserialized invokes: ${SerializationUtils.decodeInvokes(it.readBytes())}")
        }
//        val librariesAnalyzer = ReflektAnalyzer(reflektMetaFiles, bindingTrace.bindingContext, messageCollector)
//        val librariesInvokes = librariesAnalyzer.invokes()
//        messageCollector?.log("Libraries invokes: $librariesInvokes")

        val setOfFiles = files.toSet()
        val analyzer = ReflektAnalyzer(setOfFiles, bindingTrace.bindingContext, messageCollector)
        val invokes = analyzer.invokes()
        if (toSaveMetadata) {
            reflektMetaFile.writeBytes(SerializationUtils.encodeInvokes(invokes))
        }
        val uses = analyzer.uses(invokes)
        bindingTrace.saveUses(uses)

        val rootFqName = "io.kotless.dsl"
        messageCollector?.log("librariesToIntrospect: ${librariesToIntrospect}")

        if (reflektContext != null) {
            messageCollector?.log("Start analysis ${module.name} module's files")
            var sourceUses = IrReflektUses.fromReflektUses(uses, bindingTrace.bindingContext)
            (module as? ModuleDescriptorImpl) ?: error("Internal error! Can not cast a ModuleDescriptor to ModuleDescriptorImpl")
//            module.getAllSubPackages(FqName(rootFqName)).toSet().forEach {
//                module.packageFragmentProvider.packageFragments(it).map { d ->
//                    d.source
//                }.forEach{ messageCollector?.log("SOURCE 1: ${it}") }
//            }

            module.getDescriptors(module.getAllSubPackages(FqName(rootFqName)).toSet()).forEach {
                val ms = it.getMemberScope()
                messageCollector?.log("SOURCE 2: ${it.source}")
                sourceUses = sourceUses.merge(DescriptorAnalyzer(ms, messageCollector).uses(invokes))
            }
            reflektContext.uses = sourceUses
            messageCollector?.log("IrReflektUses were created successfully")

            // Need only for SmartReflekt
            val instances = getInstances(setOfFiles, bindingTrace, messageCollector = messageCollector)
            reflektContext.instances = IrReflektInstances.fromReflektInstances(instances, bindingTrace.bindingContext, messageCollector)
            messageCollector?.log("IrReflektInstances were created successfully")
            messageCollector?.log("Finish analysis ${module.name} module's files;\nUses: ${reflektContext.uses}\nInstances: ${reflektContext.instances}")
        } else {
            messageCollector?.log("Finish analysis ${module.name} module's files;\nUses: $uses")
        }

        messageCollector?.log("Start generation ReflektImpl. Base generation path: $generationPath")
        if (generationPath != null) {
            val reflektImplFile = File(generationPath, "io/reflekt/ReflektImpl.kt")
            messageCollector?.log("ReflektImpl generation path: ${reflektImplFile.absolutePath}")
            with(reflektImplFile) {
                delete()
                parentFile.mkdirs()
                writeText(
                    ReflektImplGenerator(uses).generate()
                )
            }
        }
        messageCollector?.log("Finish generation ReflektImpl")

        messageCollector?.log("Finish analysis with ReflektModuleAnalysisExtension")
        return super.analysisCompleted(project, module, bindingTrace, files)
    }
}
