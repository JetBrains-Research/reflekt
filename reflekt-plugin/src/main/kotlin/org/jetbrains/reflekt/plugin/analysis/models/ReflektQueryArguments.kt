package org.jetbrains.reflekt.plugin.analysis.models

import org.jetbrains.reflekt.plugin.analysis.processor.FileId
import org.jetbrains.kotlin.ir.types.IrType

import kotlinx.serialization.Serializable
import org.jetbrains.reflekt.ReflektClass
import kotlin.reflect.KClass
import kotlin.reflect.KVisibility

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
    val supertypes: Set<String> = emptySet(),
    val annotations: Set<String> = emptySet(),
) : ReflektQueryArguments

/**
 * @property irSignature
 * @property annotations// kotlin.FunctionN< ... > // kotlin.FunctionN< ... >
 */
data class SignatureToAnnotations(
    val irSignature: IrType?,
    val annotations: Set<String> = emptySet(),
) : ReflektQueryArguments

/**
 * @property annotations// kotlin.FunctionN< ... >
 * @property irSignature
 */
@Serializable
data class SerializableSignatureToAnnotations(
    val irSignature: SerializableIrType?,
    val annotations: Set<String> = emptySet(),
)

@Serializable
data class SerializableReflektClass(
    val annotations: Set<String>,
    val isAbstract: Boolean,
    val isCompanion: Boolean,
    val isData: Boolean,
    val isFinal: Boolean,
    val isFun: Boolean,
    val isInner: Boolean,
    val isOpen: Boolean,
    val isSealed: Boolean,
    val isValue: Boolean,
    val qualifiedName: String?,
    val superclasses: Set<String>,
    val sealedSubclasses: Set<String>,
    val simpleName: String?,
    val visibility: KVisibility?,
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
    val mentionedClasses: MutableSet<SerializableReflektClass> = HashSet(),
)
