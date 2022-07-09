@file:Suppress("KDOC_WITHOUT_RETURN_TAG")

package org.jetbrains.reflekt.plugin.utils

import org.jetbrains.reflekt.util.TypeStringRepresentationUtil

import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.*
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.types.checker.SimpleClassicTypeSystemContext.isStarProjection

import java.io.File
import java.io.PrintStream

/**
 * Common functions and constants for the plugin.
 *
 * @property messageCollector get [MessageCollector] from the [CompilerConfiguration]
 */
object Util {
    const val KOTLIN_COMPILER_PROP = "org.jetbrains.kotlin.compiler"

    val CompilerConfiguration.messageCollector: MessageCollector
        get() = this.get(
            CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY,
            MessageCollector.NONE,
        )

    fun getJarBySystemPropertyName(property: String): File = System.getProperty(property)
        ?.let(::File)
        ?.takeIf { it.exists() }
        ?: error("Property $property is not set or file under it not found")

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
}

/**
 * Converts string enum value into an instance of this enum class using [transform] function.
 *
 * @param values possible enum values
 * @param transform
 * @return [T]
 */
inline fun <T : Enum<T>> String.toEnum(values: Array<T>, transform: T.() -> String): T =
    values.first { it.transform() == this }

/**
 * String representation for [IrType].
 * Should be the same as the string representation for KType.
 *
 * @return [String]
 */
fun IrType.stringRepresentation(): String {
    val fqName = this.classFqName?.asString() ?: error("IrType does not have classFqName")
    val typeArguments = (this as? IrSimpleType)?.arguments?.map {
        // We should use * symbol in start projection instead of bounds according to KType representation
        if (it.isStarProjection()) {
            TypeStringRepresentationUtil.STAR_SYMBOL
        } else {
            val type = it.typeOrNull ?: error("Type argument does not have type!")
            TypeStringRepresentationUtil.markAsNullable(type.stringRepresentation(), type.isMarkedNullable())
        }
    } ?: emptyList()
    return TypeStringRepresentationUtil.getStringRepresentation(fqName, typeArguments)
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
