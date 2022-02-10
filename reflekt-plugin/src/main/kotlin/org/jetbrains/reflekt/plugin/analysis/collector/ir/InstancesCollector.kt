package org.jetbrains.reflekt.plugin.analysis.collector.ir

import org.jetbrains.reflekt.plugin.analysis.analyzer.ir.IrInstancesAnalyzer
import org.jetbrains.reflekt.plugin.utils.Util.log

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrDeclarationBase
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.util.file
import org.jetbrains.kotlin.ir.util.nameForIrSerialization

/**
 * A collector for searching and collecting all classes, objects, and functions in the project
 *
 * @param irInstancesAnalyzer analyzer that check if the current IR element satisfy a condition,
 *  e.g. is a top level function
 *  @param messageCollector
 */
class InstancesCollector(
    private val irInstancesAnalyzer: IrInstancesAnalyzer,
    messageCollector: MessageCollector? = null,
) : BaseCollector(irInstancesAnalyzer, messageCollector) {
    override fun visitDeclaration(declaration: IrDeclarationBase) {
        messageCollector?.log("Start checking declaration: ${declaration.nameForIrSerialization}")
        irInstancesAnalyzer.process(declaration, declaration.file)
        messageCollector?.log("Finish checking declaration: ${declaration.nameForIrSerialization}")
    }
}

/**
 * An compiler plugin extension for searching and collection all classes, objects, and functions
 */
class InstancesCollectorExtension(
    private val irInstancesAnalyzer: IrInstancesAnalyzer,
    private val messageCollector: MessageCollector? = null,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        InstancesCollector(irInstancesAnalyzer, messageCollector)
    }
}
