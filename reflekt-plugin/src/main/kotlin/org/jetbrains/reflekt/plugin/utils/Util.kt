@file:Suppress("KDOC_WITHOUT_RETURN_TAG")

package org.jetbrains.reflekt.plugin.utils

import org.jetbrains.reflekt.plugin.analysis.analyzer.source.SmartReflektAnalyzer
import org.jetbrains.reflekt.plugin.analysis.models.psi.ReflektInstances
import org.jetbrains.reflekt.plugin.analysis.models.psi.ReflektUses
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

/**
 * Common functions and constants for the plugin
 *
 * @property USES_STORE_NAME the name for [ReflektUses] to store in the [BindingContext].
 *  ReflektUses store arguments from the Reflekt queries in this case
 * @property INSTANCES_STORE_NAME the name for [ReflektInstances] to store in the [BindingContext].
 *  ReflektInstances store all instances (entities) of classes/objects/functions in the project in this case
 * @property GET_USES new [WritableSlice] to store [ReflektUses] in the [BindingContext]
 * @property GET_INSTANCES new [WritableSlice] to store [ReflektInstances] in the [BindingContext]
 * @property messageCollector get [MessageCollector] from the [CompilerConfiguration]
 */
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

    /**
     * Create new empty file for the new instance of [MessageCollector]
     *
     * @param filePath
     */
    fun CompilerConfiguration.initMessageCollector(filePath: String) {
        val file = File(filePath)
        file.createNewFile()
        this.put(
            CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY,
            PrintingMessageCollector(PrintStream(file.outputStream()), MessageRenderer.PLAIN_FULL_PATHS, true),
        )
    }

    /**
     * Log new message by [MessageCollector]
     *
     * @param message
     */
    fun MessageCollector.log(message: String) {
        this.report(
            CompilerMessageSeverity.LOGGING,
            "Reflekt: $message",
            CompilerMessageLocation.create(null),
        )
    }

    /**
     * Save [ReflektUses] into the [BindingContext]
     *
     * @param uses
     */
    internal fun BindingTrace.saveUses(uses: ReflektUses) = record(GET_USES, USES_STORE_NAME, uses)

    /**
     * Save [ReflektInstances] into the [BindingContext]
     */
    private fun BindingTrace.saveInstances(instances: ReflektInstances) = record(GET_INSTANCES, INSTANCES_STORE_NAME, instances)

    /**
     * Analyze all [files] and extract all instances (entities) of classes/objects/functions
     *
     * @param files set of [KtFile]
     * @param bindingTrace current [BindingTrace] with the [BindingContext]
     * @param toSave indicates if instances (entities) should be stored into [BindingContext]
     * @param messageCollector
     * @return [ReflektInstances]
     */
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

/**
 * Convert string enum value into an instance of this enum class by the [transform] function
 *
 * @param values possible enum values
 * @param transform
 * @return [T]
 */
fun <T : Enum<T>> String.toEnum(values: Array<T>, transform: T.() -> String): T =
    values.first { it.transform() == this }

/**
 * String representation for [KotlinType]
 * Should be the same as the string representation for KType
 *
 * @return [String]
 */
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

/**
 * Convert string enum value into regex options by the [transform] function
 *
 * @param values possible enum values
 * @param transform
 * @return [String]
 */
fun <T : Enum<T>> enumToRegexOptions(values: Array<T>, transform: T.() -> String): String =
    "(${values.joinToString(separator = "|") { it.transform() }})"
