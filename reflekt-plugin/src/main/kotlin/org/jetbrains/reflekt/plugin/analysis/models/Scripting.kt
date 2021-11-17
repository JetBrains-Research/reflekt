package org.jetbrains.reflekt.plugin.analysis.models

import org.jetbrains.kotlin.types.KotlinType

data class SupertypesToFilters(
    val supertype: KotlinType? = null,
    val filters: List<Lambda> = emptyList(),
    val imports: List<Import> = emptyList()
)

data class Lambda(
    val body: String,
    val parameters: List<String> = listOf("it")
)

data class Import(
    val fqName: String,
    val text: String
)

data class SourceFile(
    val imports: List<Import>,
    val content: String
)

data class TypeArgumentToFilters(
    val typeArgument: KotlinType? = null,
    val typeArgumentFqName: String? = null,
    val filters: List<Lambda> = emptyList(),
    val imports: List<Import> = emptyList()
)
