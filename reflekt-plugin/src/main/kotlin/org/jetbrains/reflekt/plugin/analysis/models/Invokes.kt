package org.jetbrains.reflekt.plugin.analysis.models

import kotlinx.serialization.Serializable
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.reflekt.plugin.analysis.processor.FileID
import org.jetbrains.reflekt.plugin.analysis.processor.source.invokes.base.BaseInvokesProcessor
import org.jetbrains.reflekt.plugin.analysis.processor.source.invokes.reflekt.*
import org.jetbrains.reflekt.plugin.analysis.serialization.SerializationUtils.toKotlinType
import org.jetbrains.reflekt.plugin.analysis.serialization.SerializationUtils.toSerializableKotlinType

/**
 * If the function [withAnnotations] is called without supertypes then [supertypes] is setOf(Any::class::qualifiedName)
 * If the function [withSupertypes] is called without annotations then [annotations] is empty
 */
@Serializable
data class SupertypesToAnnotations(
    val supertypes: Set<String> = emptySet(),
    val annotations: Set<String> = emptySet()
)

data class SignatureToAnnotations(
    var signature: KotlinType?, // kotlin.FunctionN< ... >
    val annotations: Set<String> = emptySet(),
)

@Serializable
data class SerializableSignatureToAnnotations(
    var signature: SerializableKotlinType?, // kotlin.FunctionN< ... >
    val annotations: Set<String> = emptySet(),
)

typealias ClassOrObjectInvokes = MutableSet<SupertypesToAnnotations>
typealias FunctionInvokes = MutableSet<SignatureToAnnotations>
typealias SerializableFunctionInvokes = MutableSet<SerializableSignatureToAnnotations>

data class ReflektInvokes(
    override val objects: HashMap<FileID, ClassOrObjectInvokes> = HashMap(),
    override val classes: HashMap<FileID, ClassOrObjectInvokes> = HashMap(),
    override val functions: HashMap<FileID, FunctionInvokes> = HashMap()
) : BaseReflektDataByFile<ClassOrObjectInvokes, ClassOrObjectInvokes, FunctionInvokes>(objects, classes, functions) {
    companion object {
        fun createByProcessors(processors: Set<BaseInvokesProcessor<*>>) = ReflektInvokes(
            objects = processors.mapNotNull { it as? ReflektObjectInvokesProcessor }.first().fileToInvokes,
            classes = processors.mapNotNull { it as? ReflektClassInvokesProcessor }.first().fileToInvokes,
            functions = processors.mapNotNull { it as? ReflektFunctionInvokesProcessor }.first().fileToInvokes
        )
    }

    fun toSerializableReflektInvokes(): SerializableReflektInvokes =
        SerializableReflektInvokes(
            objects = objects,
            classes = classes,
            functions = functions.mapValues { l ->
                l.value.map {
                    SerializableSignatureToAnnotations(
                        annotations = it.annotations,
                        signature = it.signature?.toSerializableKotlinType()
                    )
                }.toMutableSet()
            } as HashMap
        )

    fun merge(second: ReflektInvokes) = merge(this, second) { i1: ReflektInvokes, i2: ReflektInvokes ->
        ReflektInvokes(
            objects = i1.objects.merge(i2.objects),
            classes = i1.classes.merge(i2.classes),
            functions = i1.functions.merge(i2.functions)
        )
    }
}

@Serializable
data class SerializableReflektInvokes(
    val objects: HashMap<FileID, ClassOrObjectInvokes> = HashMap(),
    val classes: HashMap<FileID, ClassOrObjectInvokes> = HashMap(),
    val functions: HashMap<FileID, SerializableFunctionInvokes> = HashMap()
)

data class ReflektInvokesWithPackages(
    val invokes: ReflektInvokes,
    val packages: Set<String>
) {
    fun toSerializableReflektInvokesWithPackages() =
        SerializableReflektInvokesWithPackages(
            invokes = invokes.toSerializableReflektInvokes(),
            packages = packages
        )
}

@Serializable
data class SerializableReflektInvokesWithPackages(
    val invokes: SerializableReflektInvokes,
    val packages: Set<String>
) {
    fun toReflektInvokesWithPackages(module: ModuleDescriptorImpl) =
        ReflektInvokesWithPackages(
            invokes = ReflektInvokes(
                objects = invokes.objects,
                classes = invokes.classes,
                functions = invokes.functions.mapValues { l ->
                    l.value.map {
                        SignatureToAnnotations(
                            annotations = it.annotations,
                            signature = it.signature?.toKotlinType(module)
                        )
                    }.toMutableSet()
                } as HashMap
            ),
            packages = packages
        )
}
