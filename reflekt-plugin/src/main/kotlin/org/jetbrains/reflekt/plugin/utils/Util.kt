@file:Suppress("KDOC_WITHOUT_RETURN_TAG")

package org.jetbrains.reflekt.plugin.utils

import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.*
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.types.checker.SimpleClassicTypeSystemContext.isStarProjection
import org.jetbrains.reflekt.InternalReflektApi
import org.jetbrains.reflekt.util.TypeStringRepresentationUtil
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
        get() = this.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)

    fun getJarBySystemPropertyName(property: String): File = System.getProperty(property)
        ?.let(::File)
        ?.takeIf { it.exists() }
        ?: error("Property $property is not set or the corresponding file doesn't exist")

    /**
     * Creates new empty file for the new instance of [MessageCollector].
     *
     * @param filePath
     */
    fun CompilerConfiguration.initMessageCollector(filePath: String) {
        this.put(
            CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY,
            FilePrintingMessageCollector(filePath, MessageRenderer.PLAIN_FULL_PATHS, true),
        )
    }

    /**
     * Logs a new message by [MessageCollector].
     *
     * @param message
     */
    fun MessageCollector.log(message: String) {
        this.report(CompilerMessageSeverity.LOGGING, "Reflekt: $message", CompilerMessageLocation.create(null))
    }
}

/**
 * Converts string enum value into an instance of this enum class using [transform] function, throws exception if no values correspond to the provided string.
 *
 * @param values possible enum values.
 * @param transform the transformation function for enum values.
 * @return an instance of [T].
 */
inline fun <T : Enum<T>> String.toEnum(values: Array<T>, transform: T.() -> String): T =
    values.first { it.transform() == this }

/**
 * Converts string enum value into an instance of this enum class using [transform] function, throws exception if no values correspond to the provided string.
 *
 * All enum constants of [T] are considered.
 *
 * @param transform the transformation function for enum values.
 * @return an instance of [T].
 */
inline fun <reified T : Enum<T>> String.toEnum(transform: T.() -> String): T =
    toEnum(T::class.java.enumConstants, transform)

/**
 * Converts string enum value into regex options using [transform] function.
 *
 * @param values possible enum values
 * @param transform
 * @return [String]
 */
inline fun <T : Enum<T>> enumToRegexOptions(values: Array<T>, crossinline transform: T.() -> String): String =
    "(${values.joinToString(separator = "|") { Regex.escape(it.transform()) }})"

/**
 * Converts string enum value into regex options using [transform] function.
 *
 * All enum constants of [T] are considered.
 *
 * @param transform
 * @return [String]
 */
inline fun <reified T : Enum<T>> enumToRegexOptions(crossinline transform: T.() -> String): String =
    enumToRegexOptions(T::class.java.enumConstants, transform)

/**
 * String representation for [IrType].
 * Should be the same as the string representation for [kotlin.reflect.KType].
 *
 * @return [String]
 */
@OptIn(InternalReflektApi::class)
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

fun IrMemberAccessExpression<*>.getValueArguments() = (0 until valueArgumentsCount).map {
    getValueArgument(it)
}

/**
 * Returns immediate superclasses of the class by filtering them from its [IrClass.superTypes].
 */
fun IrClass.getImmediateSuperclasses(): List<IrClassSymbol> = superTypes.mapNotNull { it.classOrNull }

/**
 * Returns the set of all classes related to [this] class with reflection.
 * The resulting set consists of: [this class], all superclasses of all elements of the set, all sealed subclasses of all elements of the set.
 * Private classes are excluded.
 */
fun IrClass.getReflectionKnownHierarchy(): Set<IrClassSymbol> {
    val deque = ArrayDeque<IrClassSymbol>()
    deque += symbol
    val result = LinkedHashSet<IrClassSymbol>()

    while (deque.isNotEmpty()) {
        val last = deque.removeLast()

        if (last.owner.visibility == DescriptorVisibilities.PRIVATE) {
            continue
        }

        for (irClass in last.owner.getImmediateSuperclasses() + last.owner.sealedSubclasses) {
            if (irClass.owner.visibility != DescriptorVisibilities.PRIVATE && result.add(irClass)) {
                deque += irClass
            }
        }

        result += last
    }

    return result
}
