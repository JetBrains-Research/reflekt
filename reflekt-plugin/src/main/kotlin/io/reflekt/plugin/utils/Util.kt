package io.reflekt.plugin.utils

import io.reflekt.plugin.analysis.models.ReflektInstances
import io.reflekt.plugin.analysis.models.ReflektUses
import io.reflekt.plugin.analysis.analyzer.ReflektAnalyzer
import io.reflekt.plugin.analysis.analyzer.SmartReflektAnalyzer
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.*
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.util.slicedMap.Slices
import org.jetbrains.kotlin.util.slicedMap.WritableSlice
import java.io.File
import java.io.PrintStream

object Util {
    private val GET_USES: WritableSlice<String, ReflektUses> = Slices.createSimpleSlice()
    private const val USES_STORE_NAME = "ReflektUses"

    private val GET_INSTANCES: WritableSlice<String, ReflektInstances> = Slices.createSimpleSlice()
    private const val INSTANCES_STORE_NAME = "ReflektInstances"

    val CompilerConfiguration.messageCollector: MessageCollector
        get() = this.get(
            CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY,
            MessageCollector.NONE
        )

    fun CompilerConfiguration.initMessageCollector(filePath: String) {
        val file = File(filePath)
        file.createNewFile()
        this.put(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY,
            PrintingMessageCollector(PrintStream(file.outputStream()), MessageRenderer.PLAIN_FULL_PATHS, true))
    }

    fun MessageCollector.log(message: String) {
        this.report(
            CompilerMessageSeverity.LOGGING,
            "Reflekt: $message",
            CompilerMessageLocation.create(null)
        )
    }

    private fun BindingTrace.saveUses(uses: ReflektUses) {
        record(GET_USES, USES_STORE_NAME, uses)
    }

    fun BindingContext.getUses() = get(GET_USES, USES_STORE_NAME)

    private fun BindingTrace.saveInstances(instances: ReflektInstances) {
        record(GET_INSTANCES, INSTANCES_STORE_NAME, instances)
    }

    fun BindingContext.getInstances() = get(GET_INSTANCES, INSTANCES_STORE_NAME)

    fun getUses(files: Set<KtFile>, bindingTrace: BindingTrace, toSave: Boolean = true): ReflektUses {
        val analyzer = ReflektAnalyzer(files, bindingTrace.bindingContext)
        val invokes = analyzer.invokes()
        val uses = analyzer.uses(invokes)
        if (toSave) {
            bindingTrace.saveUses(uses)
        }
        return uses
    }

    fun getInstances(files: Set<KtFile>, bindingTrace: BindingTrace, toSave: Boolean = true): ReflektInstances {
        val analyzer = SmartReflektAnalyzer(files, bindingTrace.bindingContext)
        val instances = analyzer.instances()
        if (toSave) {
            bindingTrace.saveInstances(instances)
        }
        return instances
    }
}
