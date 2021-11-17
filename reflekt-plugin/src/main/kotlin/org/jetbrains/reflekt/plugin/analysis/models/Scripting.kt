package org.jetbrains.reflekt.plugin.analysis.models

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
 * @property typeArgument
 * @property typeArgumentFqName
 * @property filters
 * @property imports
 */
data class TypeArgumentToFilters(
    val typeArgument: KotlinType? = null,
    val typeArgumentFqName: String? = null,
    val filters: List<Lambda> = emptyList(),
    val imports: List<Import> = emptyList(),
)
