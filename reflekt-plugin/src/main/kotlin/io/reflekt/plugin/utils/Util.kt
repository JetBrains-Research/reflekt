package io.reflekt.plugin.utils

import io.reflekt.plugin.analysis.ReflektUses
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.*
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.util.slicedMap.Slices
import org.jetbrains.kotlin.util.slicedMap.WritableSlice
import java.io.File
import java.io.PrintStream

object Util {
    private val GET_USES: WritableSlice<String, ReflektUses> = Slices.createSimpleSlice<String, ReflektUses>()
    private const val USES_STORE_NAME = "ReflektUses"

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

    fun BindingTrace.saveUses(uses: ReflektUses) {
        record(GET_USES, USES_STORE_NAME, uses)
    }

    fun BindingContext.getUses() = get(GET_USES, USES_STORE_NAME)
}
