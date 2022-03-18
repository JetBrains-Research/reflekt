@file:Suppress("KDOC_WITHOUT_RETURN_TAG")

package org.jetbrains.reflekt.plugin.utils

import org.jetbrains.reflekt.plugin.analysis.models.psi.ReflektUses
import org.jetbrains.reflekt.plugin.utils.Util.GET_USES
import org.jetbrains.reflekt.plugin.utils.Util.USES_STORE_NAME
import org.jetbrains.reflekt.util.TypeStringRepresentationUtil

import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.*
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.resolve.*
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.util.slicedMap.Slices
import org.jetbrains.kotlin.util.slicedMap.WritableSlice

import java.io.File
import java.io.PrintStream

/**
 * Common functions and constants for the plugin.
 *
 * @property USES_STORE_NAME the name for [ReflektUses] to store in the [BindingContext].
 *  [ReflektUses] store arguments from the Reflekt queries in this case
 *  [ReflektInstances] store all instances (entities) of classes/objects/functions in the project in this case
 * @property GET_USES new [WritableSlice] to store [ReflektUses] in the [BindingContext]
 * @property messageCollector get [MessageCollector] from the [CompilerConfiguration]
 */
object Util {
    private const val USES_STORE_NAME = "ReflektUses"
    private val GET_USES: WritableSlice<String, ReflektUses> = Slices.createSimpleSlice()

    val CompilerConfiguration.messageCollector: MessageCollector
        get() = this.get(
            CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY,
            MessageCollector.NONE,
        )

    /**
     * Creates new empty file for the new instance of [MessageCollector].
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
     * Logs new message by [MessageCollector].
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
     * Saves [ReflektUses] into the [BindingContext].
     *
     * @param uses
     */
    internal fun BindingTrace.saveUses(uses: ReflektUses) = record(GET_USES, USES_STORE_NAME, uses)
}

/**
 * Converts string enum value into an instance of this enum class using [transform] function.
 *
 * @param values possible enum values
 * @param transform
 * @return [T]
 */
fun <T : Enum<T>> String.toEnum(values: Array<T>, transform: T.() -> String): T =
    values.first { it.transform() == this }

/**
 * String representation for [KotlinType].
 * Should be the same as the string representation for KType.
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
 * Converts string enum value into regex options using [transform] function.
 *
 * @param values possible enum values
 * @param transform
 * @return [String]
 */
fun <T : Enum<T>> enumToRegexOptions(values: Array<T>, transform: T.() -> String): String =
    "(${values.joinToString(separator = "|") { it.transform() }})"
