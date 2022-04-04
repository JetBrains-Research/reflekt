package org.jetbrains.reflekt.plugin.analysis

import org.jetbrains.reflekt.plugin.analysis.analyzer.descriptor.DescriptorAnalyzer
import org.jetbrains.reflekt.plugin.analysis.models.ir.IrReflektUses
import org.jetbrains.reflekt.plugin.analysis.models.psi.*
import org.jetbrains.reflekt.plugin.analysis.resolve.getDescriptors
import org.jetbrains.reflekt.plugin.analysis.serialization.SerializationUtils
import org.jetbrains.reflekt.plugin.utils.Util.log

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.BindingContext

import java.io.File

class ExternalLibrariesAnalyzer(
    private val reflektMetaFiles: Set<File>,
    private val module: ModuleDescriptorImpl,
    private val messageCollector: MessageCollector? = null,
) {
    val invokesWithPackages = getReflektMeta()

    private fun getReflektMeta(): ReflektInvokesWithPackages {
        messageCollector?.log("reflektMetaFiles $reflektMetaFiles")
        var libraryInvokes = ReflektInvokes()
        val packages = mutableSetOf<String>()
        reflektMetaFiles.forEach {
//            val currentInvokesWithPackages = SerializationUtils.decodeInvokes(it.readBytes(), module)
//            messageCollector?.log("Deserialized invokes: ${currentInvokesWithPackages.invokes}")
//            messageCollector?.log("Deserialized packages: ${currentInvokesWithPackages.packages}")
//            libraryInvokes = libraryInvokes.merge(currentInvokesWithPackages.invokes)
//            packages.addAll(currentInvokesWithPackages.packages)
        }
        messageCollector?.log("Library invokes: $libraryInvokes")
        messageCollector?.log("Library packages: $packages")
        return ReflektInvokesWithPackages(
            invokes = libraryInvokes,
            packages = packages,
        )
    }

    private fun getUses(): IrReflektUses {
        var uses = IrReflektUses()
        if (invokesWithPackages.invokes.isEmpty()) {
            return uses
        }
        module.getDescriptors(invokesWithPackages.packages.map { FqName(it) }.toSet()).forEach {
            val ms = it.getMemberScope()
            val currentUses = DescriptorAnalyzer(ms, messageCollector).uses(invokesWithPackages.invokes)
            messageCollector?.log("CURRENT LIBRARY USES: $currentUses")
            uses = uses.merge(currentUses)
        }
        return uses
    }

    fun buildIrReflektUses(
        projectUses: ReflektUses,
        binding: BindingContext,
    ): IrReflektUses? {
        messageCollector?.log("Start analysis ${module.name} module's files")
        val sourceUses = IrReflektUses.fromReflektUses(projectUses, binding)
        val librariesUses = getUses()
        val mergedUses = sourceUses.merge(librariesUses)
        messageCollector?.log("IrReflektUses were created successfully")
        if (mergedUses.isEmpty()) {
            return null
        }
        return mergedUses
    }
}
