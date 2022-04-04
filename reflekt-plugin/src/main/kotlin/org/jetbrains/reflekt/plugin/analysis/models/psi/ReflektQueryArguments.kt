package org.jetbrains.reflekt.plugin.analysis.models.psi

import kotlinx.serialization.Serializable
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.reflekt.plugin.analysis.models.*
import org.jetbrains.reflekt.plugin.analysis.processor.FileId
import org.jetbrains.reflekt.plugin.analysis.serialization.SerializationUtils.toIrType
import org.jetbrains.reflekt.plugin.analysis.serialization.SerializationUtils.toSerializableIrType

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
// TODO: use IrType
data class SignatureToAnnotations(
    val irSignature: IrType?,
    val annotations: Set<String> = emptySet(),
) : ReflektQueryArguments

/**
 * @property signature
 * @property annotations// kotlin.FunctionN< ... >
 */
@Serializable
data class SerializableSignatureToAnnotations(
    val irSignature: SerializableIrType?,
    val annotations: Set<String> = emptySet(),
)

/**
 * @property objects
 * @property classes
 * @property functions
 */
data class ReflektInvokes(
    override val objects: HashMap<FileId, ClassOrObjectQueryArguments> = HashMap(),
    override val classes: HashMap<FileId, ClassOrObjectQueryArguments> = HashMap(),
    override val functions: HashMap<FileId, FunctionQueryArguments> = HashMap(),
) : BaseReflektDataByFile<ClassOrObjectQueryArguments, ClassOrObjectQueryArguments, FunctionQueryArguments>(
    objects,
    classes,
    functions) {
    fun toSerializableReflektInvokes(): SerializableReflektQueryArguments =
        SerializableReflektQueryArguments(
            objects = objects,
            classes = classes,
            functions = functions.mapValues { fileToInvokes ->
                fileToInvokes.value.map {
                    SerializableSignatureToAnnotations(
                        annotations = it.annotations,
                        irSignature = it.irSignature?.toSerializableIrType(),
                    )
                }.toMutableSet()
            } as HashMap,
        )
}

/**
 * @property objects
 * @property classes
 * @property functions
 */
@Serializable
data class SerializableReflektQueryArguments(
    val objects: HashMap<FileId, ClassOrObjectQueryArguments> = HashMap(),
    val classes: HashMap<FileId, ClassOrObjectQueryArguments> = HashMap(),
    val functions: HashMap<FileId, SerializableFunctionQueryArguments> = HashMap(),
)

/**
 * @property invokes
 * @property packages
 */
data class ReflektInvokesWithPackages(
    val invokes: ReflektInvokes,
    val packages: Set<String>,
) {
    fun toSerializableReflektInvokesWithPackages() =
        SerializableReflektInvokesWithPackages(
            invokes = invokes.toSerializableReflektInvokes(),
            packages = packages,
        )
}

/**
 * @property invokes
 * @property packages
 */
@Serializable
data class SerializableReflektInvokesWithPackages(
    val invokes: SerializableReflektQueryArguments,
    val packages: Set<String>,
) {
    fun toReflektInvokesWithPackages(pluginContext: IrPluginContext) =
        ReflektInvokesWithPackages(
            invokes = ReflektInvokes(
                objects = invokes.objects,
                classes = invokes.classes,
                functions = invokes.functions.mapValues { fileToInvokes ->
                    fileToInvokes.value.map {
                        SignatureToAnnotations(
                            annotations = it.annotations,
                            irSignature = it.irSignature?.toIrType(pluginContext)
                        )
                    }.toMutableSet()
                } as HashMap,
            ),
            packages = packages,
        )
}
