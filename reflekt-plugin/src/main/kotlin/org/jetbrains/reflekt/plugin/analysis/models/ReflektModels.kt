package org.jetbrains.reflekt.plugin.analysis.models

import kotlinx.serialization.Serializable
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.reflekt.plugin.analysis.processor.FileID
import org.jetbrains.reflekt.plugin.analysis.processor.source.invokes.*
import org.jetbrains.reflekt.plugin.analysis.processor.source.uses.*
import org.jetbrains.reflekt.plugin.analysis.psi.function.toFunctionInfo
import org.jetbrains.reflekt.plugin.analysis.serialization.*
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

@Serializable
data class SerializableKotlinType(
    val fqName: String,
    val arguments: List<SerializableTypeProjection> = emptyList(),
    val returnType: String,
    val receiverType: SerializableKotlinType?
)

@Serializable
data class SerializableTypeProjection(
    val fqName: String,
    val isStarProjection: Boolean,
    val projectionKind: Variance,
)

@Serializable
data class SerializableSignatureToAnnotations(
    var signature: SerializableKotlinType?, // kotlin.FunctionN< ... >
    val annotations: Set<String> = emptySet(),
)

data class SignatureToAnnotations(
    var signature: KotlinType?, // kotlin.FunctionN< ... >
    val annotations: Set<String> = emptySet(),
)

typealias ClassOrObjectInvokes = MutableSet<SupertypesToAnnotations>
typealias FunctionInvokes = MutableSet<SignatureToAnnotations>
typealias SerializableFunctionInvokes = MutableSet<SerializableSignatureToAnnotations>

@Serializable
data class SerializableReflektInvokes(
    val objects: HashMap<FileID, ClassOrObjectInvokes> = HashMap(),
    val classes: HashMap<FileID, ClassOrObjectInvokes> = HashMap(),
    val functions: HashMap<FileID, SerializableFunctionInvokes> = HashMap()
)

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

data class ReflektInvokes(
    val objects: HashMap<FileID, ClassOrObjectInvokes> = HashMap(),
    val classes: HashMap<FileID, ClassOrObjectInvokes> = HashMap(),
    val functions: HashMap<FileID, FunctionInvokes> = HashMap()
) {
    companion object {
        fun createByProcessors(processors: Set<BaseInvokesProcessor<*>>) = ReflektInvokes(
            objects = processors.mapNotNull { it as? ObjectInvokesProcessor }.first().fileToInvokes,
            classes = processors.mapNotNull { it as? ClassInvokesProcessor }.first().fileToInvokes,
            functions = processors.mapNotNull { it as? FunctionInvokesProcessor }.first().fileToInvokes
        )
    }

    fun isEmpty() = objects.isEmpty() && classes.isEmpty() && functions.isEmpty()

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

    fun merge(second: ReflektInvokes): ReflektInvokes = ReflektInvokes(
        objects = this.objects.merge(second.objects),
        classes = this.classes.merge(second.classes),
        functions = this.functions.merge(second.functions)
    )

    private fun <V> HashMap<FileID, MutableSet<V>>.merge(second: HashMap<FileID, MutableSet<V>>): HashMap<FileID, MutableSet<V>> =
        this.also { second.forEach { (k, v) -> this.getOrPut(k) { v } } }
}

typealias TypeUses<K, V> = MutableMap<K, MutableList<V>>
typealias ClassOrObjectUses = TypeUses<SupertypesToAnnotations, KtClassOrObject>
typealias FunctionUses = TypeUses<SignatureToAnnotations, KtNamedFunction>

/* Stores enough information to generate function reference IR */
data class IrFunctionInfo(
    val fqName: String,
    val receiverFqName: String?,
    val isObjectReceiver: Boolean
)

typealias IrClassOrObjectUses = TypeUses<SupertypesToAnnotations, String>
typealias IrFunctionUses = TypeUses<SignatureToAnnotations, IrFunctionInfo>

fun ClassOrObjectUses.toSupertypesToFqNamesMap(): Map<Set<String>, List<String>> {
    return this.map { it.key.supertypes to it.value.mapNotNull { it.fqName?.toString() } }.toMap()
}

fun FunctionUses.toAnnotationsToFunction(): Map<Set<String>, List<KtNamedFunction>> {
    return this.map { it.key.annotations to it.value }.toMap()
}

/**
 * Store a set of qualified names that match the conditions for each item from [ReflektInvokes]
 */
data class ReflektUses(
    val objects: HashMap<FileID, ClassOrObjectUses> = HashMap(),
    val classes: HashMap<FileID, ClassOrObjectUses> = HashMap(),
    val functions: HashMap<FileID, FunctionUses> = HashMap()
) {
    companion object {
        fun createByProcessors(processors: Set<BaseUsesProcessor<*>>) = ReflektUses(
            objects = processors.mapNotNull { it as? ObjectUsesProcessor }.first().fileToUses,
            classes = processors.mapNotNull { it as? ClassUsesProcessor }.first().fileToUses,
            functions = processors.mapNotNull { it as? FunctionUsesProcessor }.first().fileToUses
        )
    }
}

fun <T, V : KtElement> HashMap<FileID, TypeUses<T, V>>.flatten(): TypeUses<T, V> {
    val uses: TypeUses<T, V> = HashMap()
    this.forEach { (_, typeUses) ->
        typeUses.forEach { (k, v) ->
            uses.getOrPut(k) { mutableListOf() }.addAll(v)
        }
    }
    return uses
}

data class IrReflektUses(
    val objects: IrClassOrObjectUses = HashMap(),
    val classes: IrClassOrObjectUses = HashMap(),
    val functions: IrFunctionUses = HashMap()
) {
    companion object {
        fun fromReflektUses(uses: ReflektUses, binding: BindingContext) = IrReflektUses(
            objects = HashMap(uses.objects.flatten().mapValues { (_, v) -> v.map { it.fqName!!.toString() }.toMutableList() }),
            classes = HashMap(uses.classes.flatten().mapValues { (_, v) -> v.map { it.fqName!!.toString() }.toMutableList() }),
            functions = HashMap(uses.functions.flatten().mapValues { (_, v) -> v.map { it.toFunctionInfo(binding) }.toMutableList() }),
        )
    }

    fun merge(second: IrReflektUses): IrReflektUses = IrReflektUses(
        objects = this.objects.merge(second.objects),
        classes = this.classes.merge(second.classes),
        functions = this.functions.merge(second.functions)
    )

    private fun <K, V> TypeUses<K, V>.merge(second: TypeUses<K, V>): TypeUses<K, V> = this.also { second.forEach { (k, v) -> this.getOrPut(k) { v } } }
}