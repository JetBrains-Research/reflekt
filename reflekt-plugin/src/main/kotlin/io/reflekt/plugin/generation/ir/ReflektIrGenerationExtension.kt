package io.reflekt.plugin.generation.ir

import io.reflekt.plugin.analysis.ir.ReflektIrInvokesAnalyzer
import io.reflekt.plugin.analysis.ir.ReflektIrUsesAnalyzer
import io.reflekt.plugin.utils.Util.log
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

class ReflektIrGenerationExtension(private val messageCollector: MessageCollector? = null) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        messageCollector?.log("[IR] Start ${moduleFragment.name} module analysis")
        val invokes = ReflektIrInvokesAnalyzer.collectInvokes(moduleFragment, messageCollector)
        val uses = ReflektIrUsesAnalyzer.collectUses(moduleFragment, invokes, messageCollector)
        messageCollector?.log("[IR] Finish ${moduleFragment.name} module analysis\nUses: $uses")
        moduleFragment.transform(ReflektIrTransformer(messageCollector, pluginContext, uses), null)
    }
}
