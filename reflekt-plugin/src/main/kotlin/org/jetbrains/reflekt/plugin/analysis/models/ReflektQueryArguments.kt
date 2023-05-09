package org.jetbrains.reflekt.plugin.analysis.models

import kotlinx.serialization.Serializable
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.reflekt.plugin.analysis.processor.FileId
import org.jetbrains.reflekt.plugin.analysis.serialization.ClassIdSerializer

typealias ClassOrObjectQueryArguments = MutableSet<SupertypesToAnnotations>
typealias FunctionQueryArguments = MutableSet<SignatureToAnnotations>
typealias SerializableFunctionQueryArguments = MutableSet<SerializableSignatureToAnnotations>

interface ReflektQueryArguments

/**
 * If the Reflekt function <withAnnotations> is called without supertypes then [supertypes] is setOf(Any::class::qualifiedName)
 * If the Reflekt function <withSupertypes> is called without annotations then [annotations] is empty
 *
 * @property supertypes
 * @property annotations
 */
@Serializable
data class SupertypesToAnnotations(
    val supertypes: Set<@Serializable(with = ClassIdSerializer::class) ClassId> = emptySet(),
    val annotations: Set<@Serializable(with = ClassIdSerializer::class) ClassId> = emptySet(),
) : ReflektQueryArguments

/**
 * @property irSignature
 * @property annotations// kotlin.FunctionN< ... > // kotlin.FunctionN< ... >
 */
data class SignatureToAnnotations(
    val irSignature: IrType?,
    val annotations: Set<ClassId> = emptySet(),
) : ReflektQueryArguments

/**
 * @property annotations// kotlin.FunctionN< ... >
 * @property irSignature
 */
@Serializable
data class SerializableSignatureToAnnotations(
    val irSignature: SerializableIrType?,
    val annotations: Set<@Serializable(with = ClassIdSerializer::class) ClassId> = emptySet(),
)

/**
 * @property objects
 * @property classes
 * @property functions
 */
@Serializable
data class SerializableReflektQueryArguments(
    val objects: MutableMap<FileId, ClassOrObjectQueryArguments> = HashMap(),
    val classes: MutableMap<FileId, ClassOrObjectQueryArguments> = HashMap(),
    val functions: MutableMap<FileId, SerializableFunctionQueryArguments> = HashMap(),
)
