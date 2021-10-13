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
import org.jetbrains.kotlin.resolve.descriptorUtil.module
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
        (module as? ModuleDescriptorImpl) ?: error("Internal error! Can not cast a ModuleDescriptor to ModuleDescriptorImpl")
        messageCollector?.log("reflektMetaFiles ${reflektMetaFiles}")
        var libraryInvokes = ReflektInvokes()
        reflektMetaFiles.forEach {
            val currentInvokes = SerializationUtils.decodeInvokes(it.readBytes(), module)
            messageCollector?.log("Deserialized invokes: $currentInvokes")
            libraryInvokes = libraryInvokes.merge(currentInvokes)
        }
        messageCollector?.log("Library invokes: $libraryInvokes")

        val setOfFiles = files.toSet()
        val analyzer = ReflektAnalyzer(setOfFiles, bindingTrace.bindingContext, messageCollector)
        val invokes = analyzer.invokes()
        messageCollector?.log("Project's invokes: $invokes")
        if (toSaveMetadata) {
            messageCollector?.log("Save Reflekt meta data")
            reflektMetaFile.createNewFile()
            reflektMetaFile.writeBytes(SerializationUtils.encodeInvokes(invokes))
        }
        val mergedInvokes = invokes.merge(libraryInvokes)
        messageCollector?.log("Merged invokes: $mergedInvokes")
        val uses = analyzer.uses(mergedInvokes)
        bindingTrace.saveUses(uses)

        val rootFqName = "io.kotless.dsl"
        messageCollector?.log("librariesToIntrospect: ${librariesToIntrospect}")

        if (reflektContext != null) {
            messageCollector?.log("Start analysis ${module.name} module's files")
            var sourceUses = IrReflektUses.fromReflektUses(uses, bindingTrace.bindingContext)
            module.getDescriptors(module.getAllSubPackages(FqName(rootFqName)).toSet()).forEach {
                messageCollector?.log("SOURCE: ${it.source}")
                messageCollector?.log("containingFile: ${it.source.containingFile.name}")
                messageCollector?.log("librariesToIntrospect: ${librariesToIntrospect}")
                if (it.module.name.asString() in librariesToIntrospect) {
                    val ms = it.getMemberScope()
                    val currentUses = DescriptorAnalyzer(ms, messageCollector).uses(invokes)
                    messageCollector?.log("USES 2: ${currentUses}")
                    sourceUses = sourceUses.merge(currentUses)
                }
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
