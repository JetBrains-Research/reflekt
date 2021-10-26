package org.jetbrains.reflekt.plugin.analysis

import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisHandlerExtension
import org.jetbrains.reflekt.plugin.analysis.analyzer.descriptor.DescriptorAnalyzer
import org.jetbrains.reflekt.plugin.analysis.analyzer.source.ReflektAnalyzer
import org.jetbrains.reflekt.plugin.analysis.models.*
import org.jetbrains.reflekt.plugin.analysis.resolve.getDescriptors
import org.jetbrains.reflekt.plugin.analysis.serialization.SerializationUtils
import org.jetbrains.reflekt.plugin.generation.code.generator.ReflektImplGenerator
import org.jetbrains.reflekt.plugin.utils.Util.getInstances
import org.jetbrains.reflekt.plugin.utils.Util.log
import org.jetbrains.reflekt.plugin.utils.Util.saveUses
import java.io.File

class ReflektModuleAnalysisExtension(
    private val reflektMetaFiles: Set<File>,
    private val toSaveMetadata: Boolean,
    private val generationPath: File?,
    private val reflektMetaFile: File?,
    private val reflektContext: ReflektContext? = null,
    private val messageCollector: MessageCollector? = null
) : AnalysisHandlerExtension {

    // TODO: store ReflektMetaInf by modules
    override fun analysisCompleted(project: Project, module: ModuleDescriptor, bindingTrace: BindingTrace, files: Collection<KtFile>): AnalysisResult? {
        messageCollector?.log("ReflektAnalysisExtension is starting...")
        (module as? ModuleDescriptorImpl) ?: error("Internal error! Can not cast a ModuleDescriptor to ModuleDescriptorImpl")
        val (libraryInvokes, packages) = getReflektMeta(reflektMetaFiles, module)

        // TODO: use a const
        val setOfFiles = files.filter { it.packageFqName.asString() != "org.jetbrains.reflekt" }.toSet()
        val analyzer = ReflektAnalyzer(setOfFiles, bindingTrace.bindingContext, messageCollector)
        val invokes = analyzer.invokes()
        messageCollector?.log("Project's invokes: $invokes")
        if (toSaveMetadata && reflektMetaFile != null) {
            saveMetaData(invokes, setOfFiles)
        }
        val mergedInvokes = invokes.merge(libraryInvokes)
        messageCollector?.log("Merged invokes: $mergedInvokes")
        val uses = analyzer.uses(mergedInvokes)
        bindingTrace.saveUses(uses)

        if (reflektContext != null) {
            messageCollector?.log("Start analysis ${module.name} module's files")
            val sourceUses = IrReflektUses.fromReflektUses(uses, bindingTrace.bindingContext)
            val librariesUses = getUsesFromLibraries(module, packages, invokes)
            reflektContext.uses = sourceUses.merge(librariesUses)
            messageCollector?.log("IrReflektUses were created successfully")

            // Need only for SmartReflekt
            val instances = getInstances(setOfFiles, bindingTrace, messageCollector = messageCollector)
            reflektContext.instances = IrReflektInstances.fromReflektInstances(instances, bindingTrace.bindingContext, messageCollector)
            messageCollector?.log("IrReflektInstances were created successfully")
            messageCollector?.log("Finish analysis ${module.name} module's files;\nUses: ${reflektContext.uses}\nInstances: ${reflektContext.instances}")
        } else {
            messageCollector?.log("Finish analysis ${module.name} module's files;\nUses: $uses")
        }

        val reflektImplFile = File(generationPath, "io/reflekt/ReflektImpl.kt")
        if (toGenerateReflektImpl(libraryInvokes)) {
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

    private fun getUsesFromLibraries(module: ModuleDescriptorImpl, packages: Set<String>, invokes: ReflektInvokes): IrReflektUses {
        var uses = IrReflektUses()
        module.getDescriptors(packages.map { FqName(it) }.toSet()).forEach {
            val ms = it.getMemberScope()
            val currentUses = DescriptorAnalyzer(ms, messageCollector).uses(invokes)
            messageCollector?.log("CURRENT LIBRARY USES: $currentUses")
            uses = uses.merge(currentUses)
        }
        return uses
    }

    private fun getReflektMeta(reflektMetaFiles: Set<File>, module: ModuleDescriptorImpl): ReflektInvokesWithPackages {
        messageCollector?.log("reflektMetaFiles $reflektMetaFiles")
        var libraryInvokes = ReflektInvokes()
        val packages = mutableSetOf<String>()
        reflektMetaFiles.forEach {
            val currentInvokesWithPackages = SerializationUtils.decodeInvokes(it.readBytes(), module)
            messageCollector?.log("Deserialized invokes: ${currentInvokesWithPackages.invokes}")
            messageCollector?.log("Deserialized packages: ${currentInvokesWithPackages.packages}")
            libraryInvokes = libraryInvokes.merge(currentInvokesWithPackages.invokes)
            packages.addAll(currentInvokesWithPackages.packages)
        }
        messageCollector?.log("Library invokes: $libraryInvokes")
        messageCollector?.log("Library packages: $packages")
        return ReflektInvokesWithPackages(
            invokes = libraryInvokes,
            packages = packages
        )
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
