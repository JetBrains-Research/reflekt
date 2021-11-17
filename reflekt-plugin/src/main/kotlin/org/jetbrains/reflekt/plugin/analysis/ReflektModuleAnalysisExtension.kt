package org.jetbrains.reflekt.plugin.analysis

import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisHandlerExtension
import org.jetbrains.reflekt.plugin.analysis.analyzer.source.ReflektAnalyzer
import org.jetbrains.reflekt.plugin.analysis.models.*
import org.jetbrains.reflekt.plugin.analysis.models.ir.*
import org.jetbrains.reflekt.plugin.analysis.serialization.SerializationUtils
import org.jetbrains.reflekt.plugin.generation.code.generator.ReflektImplGenerator
import org.jetbrains.reflekt.plugin.utils.Util.getInstances
import org.jetbrains.reflekt.plugin.utils.Util.log
import org.jetbrains.reflekt.plugin.utils.Util.saveUses
import java.io.File

class ReflektModuleAnalysisExtension(
    private val reflektMetaFilesFromLibraries: Set<File>,
    private val toSaveMetadata: Boolean,
    private val generationPath: File?,
    private val reflektMetaFile: File?,
    private val reflektContext: ReflektContext? = null,
    private val messageCollector: MessageCollector? = null
) : AnalysisHandlerExtension {
    private val reflektPackage = "org.jetbrains.reflekt"

    // TODO: store ReflektMetaInf by modules
    override fun analysisCompleted(project: Project, module: ModuleDescriptor, bindingTrace: BindingTrace, files: Collection<KtFile>): AnalysisResult? {
        messageCollector?.log("ReflektAnalysisExtension is starting...")
        (module as? ModuleDescriptorImpl) ?: error("Internal error! Can not cast a ModuleDescriptor to ModuleDescriptorImpl")
        val externalLibrariesAnalyzer = ExternalLibrariesAnalyzer(reflektMetaFilesFromLibraries, module, messageCollector)

        val setOfFiles = files.filter { it.packageFqName.asString() != reflektPackage }.toSet()
        val analyzer = ReflektAnalyzer(setOfFiles, bindingTrace.bindingContext, messageCollector)
        val invokes = analyzer.invokes()
        messageCollector?.log("Project's invokes: $invokes")
        if (toSaveMetadata && reflektMetaFile != null) {
            saveMetaData(invokes, setOfFiles)
        }
        val mergedInvokes = invokes.merge(externalLibrariesAnalyzer.invokesWithPackages.invokes)
        messageCollector?.log("Merged invokes: $mergedInvokes")
        val uses = analyzer.uses(mergedInvokes)
        bindingTrace.saveUses(uses)

        if (reflektContext != null) {
            reflektContext.uses = externalLibrariesAnalyzer.buildIrReflektUses(
                uses,
                bindingTrace.bindingContext,
            )

            // TODO: collect instances only if SmartReflekt calls exist
            // Need only for SmartReflekt
            val instances = getInstances(setOfFiles, bindingTrace, messageCollector = messageCollector)
            reflektContext.instances = IrReflektInstances.fromReflektInstances(instances, bindingTrace.bindingContext, messageCollector)
            messageCollector?.log("IrReflektInstances were created successfully")
            messageCollector?.log("Finish analysis ${module.name} module's files;\nUses: ${reflektContext.uses}\nInstances: ${reflektContext.instances}")
        } else {
            messageCollector?.log("Finish analysis ${module.name} module's files;\nUses: $uses")
        }

        val reflektImplFile = File(generationPath, "io/reflekt/ReflektImpl.kt")
        if (toGenerateReflektImpl(externalLibrariesAnalyzer.invokesWithPackages.invokes)) {
            generateReflektImpl(uses, reflektImplFile)
        }
        messageCollector?.log("Finish analysis with ReflektModuleAnalysisExtension")

        return super.analysisCompleted(project, module, bindingTrace, files)
    }

    private fun toGenerateReflektImpl(libraryInvokes: ReflektInvokes) = !libraryInvokes.isEmpty()

    // TODO: generate ReflektImpl by IrReflektUses
    private fun generateReflektImpl(uses: ReflektUses, reflektImplFile: File) {
        messageCollector?.log("Start generation ReflektImpl. Base generation path: $generationPath")
        messageCollector?.log("ReflektImpl generation path: ${reflektImplFile.absolutePath}")
        with(reflektImplFile) {
            delete()
            parentFile.mkdirs()
            writeText(
                ReflektImplGenerator(uses).generate()
            )
        }
        messageCollector?.log("Finish generation ReflektImpl")
    }

    private fun saveMetaData(invokes: ReflektInvokes, files: Set<KtFile>) {
        messageCollector?.log("Save Reflekt meta data")
        reflektMetaFile!!.createNewFile()
        reflektMetaFile.writeBytes(
            SerializationUtils.encodeInvokes(
                ReflektInvokesWithPackages(
                    invokes = invokes,
                    packages = files.map { it.packageFqName.asString() }.toSet()
                )
            )
        )
    }
}
