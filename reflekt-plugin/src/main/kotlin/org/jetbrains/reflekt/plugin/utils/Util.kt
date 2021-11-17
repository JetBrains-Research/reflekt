package org.jetbrains.reflekt.plugin.utils

import org.jetbrains.reflekt.plugin.analysis.analyzer.source.ReflektAnalyzer
import org.jetbrains.reflekt.plugin.analysis.analyzer.source.SmartReflektAnalyzer
import org.jetbrains.reflekt.plugin.analysis.models.ReflektInstances
import org.jetbrains.reflekt.plugin.analysis.models.ReflektUses
import org.jetbrains.reflekt.util.TypeStringRepresentationUtil

import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.*
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.util.slicedMap.Slices
import org.jetbrains.kotlin.util.slicedMap.WritableSlice

import java.io.File
import java.io.PrintStream

object Util {
    private const val USES_STORE_NAME = "ReflektUses"
    private const val INSTANCES_STORE_NAME = "ReflektInstances"
    private val GET_USES: WritableSlice<String, ReflektUses> = Slices.createSimpleSlice()
    private val GET_INSTANCES: WritableSlice<String, ReflektInstances> = Slices.createSimpleSlice()

    val CompilerConfiguration.messageCollector: MessageCollector
        get() = this.get(
            CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY,
            MessageCollector.NONE,
        )

    fun CompilerConfiguration.initMessageCollector(filePath: String) {
        val file = File(filePath)
        file.createNewFile()
        this.put(
            CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY,
            PrintingMessageCollector(PrintStream(file.outputStream()), MessageRenderer.PLAIN_FULL_PATHS, true),
        )
    }

    fun MessageCollector.log(message: String) {
        this.report(
            CompilerMessageSeverity.LOGGING,
            "Reflekt: $message",
            CompilerMessageLocation.create(null),
        )
    }

    internal fun BindingTrace.saveUses(uses: ReflektUses) {
        record(GET_USES, USES_STORE_NAME, uses)
    }

    private fun BindingTrace.saveInstances(instances: ReflektInstances) {
        record(GET_INSTANCES, INSTANCES_STORE_NAME, instances)
    }

    fun getUses(
        files: Set<KtFile>,
        bindingTrace: BindingTrace,
        toSave: Boolean = true): ReflektUses {
        val analyzer = ReflektAnalyzer(files, bindingTrace.bindingContext)
        val invokes = analyzer.invokes()
        val uses = analyzer.uses(invokes)
        if (toSave) {
            bindingTrace.saveUses(uses)
        }
        return uses
    }

    fun getInstances(
        files: Set<KtFile>,
        bindingTrace: BindingTrace,
        toSave: Boolean = true,
        messageCollector: MessageCollector? = null): ReflektInstances {
        val analyzer = SmartReflektAnalyzer(files, bindingTrace.bindingContext, messageCollector)
        val instances = analyzer.instances()
        if (toSave) {
            bindingTrace.saveInstances(instances)
        }
        return instances
    }
}

fun <T : Enum<T>> String.toEnum(values: Array<T>, transform: T.() -> String): T =
    values.first { it.transform() == this }

fun KotlinType.stringRepresentation(): String {
    val declaration = requireNotNull(constructor.declarationDescriptor) {
        "declarationDescriptor is null for constructor = $constructor with ${constructor.javaClass}"
    }
    val typeArguments = arguments.map {
        // We should use * symbol in start projection instead of bounds according to KType representation
        if (it.isStarProjection) {
            TypeStringRepresentationUtil.STAR_SYMBOL
        } else {
            TypeStringRepresentationUtil.markAsNullable(it.type.stringRepresentation(), it.type.isMarkedNullable)
        }
    }
    return TypeStringRepresentationUtil.getStringRepresentation(DescriptorUtils.getFqName(declaration).asString(), typeArguments)
}

fun <T : Enum<T>> enumToRegexOptions(values: Array<T>, transform: T.() -> String): String =
    "(${values.joinToString(separator = "|") { it.transform() }})"
