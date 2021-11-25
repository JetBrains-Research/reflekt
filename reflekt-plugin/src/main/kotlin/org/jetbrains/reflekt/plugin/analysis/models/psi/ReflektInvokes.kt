package org.jetbrains.reflekt.plugin.analysis.models.psi

import org.jetbrains.reflekt.plugin.analysis.models.*
import org.jetbrains.reflekt.plugin.analysis.processor.FileId
import org.jetbrains.reflekt.plugin.analysis.processor.source.invokes.base.BaseInvokesProcessor
import org.jetbrains.reflekt.plugin.analysis.processor.source.invokes.reflekt.*
import org.jetbrains.reflekt.plugin.analysis.serialization.SerializationUtils.toKotlinType
import org.jetbrains.reflekt.plugin.analysis.serialization.SerializationUtils.toSerializableKotlinType

import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl
import org.jetbrains.kotlin.types.KotlinType

import kotlinx.serialization.Serializable

typealias ClassOrObjectInvokes = MutableSet<SupertypesToAnnotations>
typealias FunctionInvokes = MutableSet<SignatureToAnnotations>
typealias SerializableFunctionInvokes = MutableSet<SerializableSignatureToAnnotations>

typealias BaseInvokeProcessors = Set<BaseInvokesProcessor<*>>

/**
 * If the function [withAnnotations] is called without supertypes then [supertypes] is setOf(Any::class::qualifiedName)
 * If the function [withSupertypes] is called without annotations then [annotations] is empty
 * @property supertypes
 * @property annotations
 */
@Serializable
data class SupertypesToAnnotations(
    val supertypes: Set<String> = emptySet(),
    val annotations: Set<String> = emptySet(),
)

/**
 * @property signature
 * @property annotations// kotlin.FunctionN< ... > // kotlin.FunctionN< ... >
 */
data class SignatureToAnnotations(
    var signature: KotlinType?, val annotations: Set<String> = emptySet(),
)

/**
 * @property signature
 * @property annotations// kotlin.FunctionN< ... >
 */
@Serializable
data class SerializableSignatureToAnnotations(
    val signature: SerializableKotlinType?,
    val annotations: Set<String> = emptySet(),
)

/**
 * @property objects
 * @property classes
 * @property functions
 */
data class ReflektInvokes(
    override val objects: HashMap<FileId, ClassOrObjectInvokes> = HashMap(),
    override val classes: HashMap<FileId, ClassOrObjectInvokes> = HashMap(),
    override val functions: HashMap<FileId, FunctionInvokes> = HashMap(),
) : BaseReflektDataByFile<ClassOrObjectInvokes, ClassOrObjectInvokes, FunctionInvokes>(
    objects,
    classes,
    functions) {
    fun toSerializableReflektInvokes(): SerializableReflektInvokes =
        SerializableReflektInvokes(
            objects = objects,
            classes = classes,
            functions = functions.mapValues { fileToInvokes ->
                fileToInvokes.value.map {
                    SerializableSignatureToAnnotations(
                        annotations = it.annotations,
                        signature = it.signature?.toSerializableKotlinType(),
                    )
                }.toMutableSet()
            } as HashMap,
        )

    @Suppress("TYPE_ALIAS", "VARIABLE_NAME_INCORRECT")
    fun merge(second: ReflektInvokes) = org.jetbrains.reflekt.plugin.analysis.models.merge(this, second) { i1: ReflektInvokes, i2: ReflektInvokes ->
        ReflektInvokes(
            objects = i1.objects.merge(i2.objects) { mutableSetOf() },
            classes = i1.classes.merge(i2.classes) { mutableSetOf() },
            functions = i1.functions.merge(i2.functions) { mutableSetOf() },
        )
    }
    companion object {
        fun createByProcessors(processors: BaseInvokeProcessors) = ReflektInvokes(
            objects = processors.mapNotNull { it as? ReflektObjectInvokesProcessor }.first().fileToInvokes,
            classes = processors.mapNotNull { it as? ReflektClassInvokesProcessor }.first().fileToInvokes,
            functions = processors.mapNotNull { it as? ReflektFunctionInvokesProcessor }.first().fileToInvokes,
        )
    }
}

/**
 * @property objects
 * @property classes
 * @property functions
 */
@Serializable
data class SerializableReflektInvokes(
    val objects: HashMap<FileId, ClassOrObjectInvokes> = HashMap(),
    val classes: HashMap<FileId, ClassOrObjectInvokes> = HashMap(),
    val functions: HashMap<FileId, SerializableFunctionInvokes> = HashMap(),
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
    val invokes: SerializableReflektInvokes,
    val packages: Set<String>,
) {
    fun toReflektInvokesWithPackages(module: ModuleDescriptorImpl) =
        ReflektInvokesWithPackages(
            invokes = ReflektInvokes(
                objects = invokes.objects,
                classes = invokes.classes,
                functions = invokes.functions.mapValues { fileToInvokes ->
                    fileToInvokes.value.map {
                        SignatureToAnnotations(
                            annotations = it.annotations,
                            signature = it.signature?.toKotlinType(module),
                        )
                    }.toMutableSet()
                } as HashMap,
            ),
            packages = packages,
        )
}
