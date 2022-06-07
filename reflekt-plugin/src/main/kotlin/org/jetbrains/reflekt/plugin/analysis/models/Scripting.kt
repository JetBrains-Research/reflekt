package org.jetbrains.reflekt.plugin.analysis.models

import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.types.KotlinType

/**
 * @property supertype
 * @property filters
 * @property imports
 */
data class SupertypesToFilters(
    val supertype: KotlinType? = null,
    val filters: List<Lambda> = emptyList(),
    val imports: List<Import> = emptyList(),
)

/**
 * @property body
 * @property parameters
 */
data class Lambda(
    val body: String,
    val parameters: List<String> = listOf("it"),
)

/**
 * @property fqName
 * @property text
 */
data class Import(
    val fqName: String,
    val text: String,
)

/**
 * @property imports
 * @property content
 */
data class SourceFile(
    val imports: List<Import>,
    val content: String,
)

/**
 * Represents filter conditions from SmartReflekt lambdas from one query
 *
 * @property filters list of filters from all lambdas
 * @property imports the list of imports from the file with this query
 * @property irTypeArgument function signature to search
 */
data class TypeArgumentToFilters(
    val filters: List<Lambda> = emptyList(),
    val imports: List<Import> = emptyList(),
    var irTypeArgument: IrType? = null,
)
