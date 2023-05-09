package org.jetbrains.reflekt.plugin.analysis.serialization

import kotlinx.serialization.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.jetbrains.kotlin.name.*

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = FqName::class)
object FqNameSerializer : KSerializer<FqName> {
    override val descriptor: SerialDescriptor = String.serializer().descriptor
    override fun deserialize(decoder: Decoder): FqName = FqName(decoder.decodeString())
    override fun serialize(encoder: Encoder, value: FqName) = encoder.encodeString(value.asString())
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Name::class)
object NameSerializer : KSerializer<Name> {
    override val descriptor: SerialDescriptor = NameSurrogate.serializer().descriptor
    override fun deserialize(decoder: Decoder): Name {
        val surrogate = decoder.decodeSerializableValue(NameSurrogate.serializer())
        return if (surrogate.isSpecial) {
            Name.special(surrogate.name)
        } else {
            Name.identifier(surrogate.name)
        }
    }

    override fun serialize(encoder: Encoder, value: Name) =
        encoder.encodeSerializableValue(NameSurrogate.serializer(), NameSurrogate(value.asString(), value.isSpecial))
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = ClassId::class)
object ClassIdSerializer : KSerializer<ClassId> {
    override val descriptor: SerialDescriptor = ClassIdSurrogate.serializer().descriptor

    override fun deserialize(decoder: Decoder): ClassId {
        val surrogate = decoder.decodeSerializableValue(ClassIdSurrogate.serializer())
        return ClassId(surrogate.packageFqName, surrogate.relativeClassName, surrogate.isLocal)
    }

    override fun serialize(encoder: Encoder, value: ClassId) {
        val surrogate = ClassIdSurrogate(value.packageFqName, value.relativeClassName, value.isLocal)
        encoder.encodeSerializableValue(ClassIdSurrogate.serializer(), surrogate)
    }
}

@OptIn(ExperimentalSerializationApi::class)
object CallableIdSerializer : KSerializer<CallableId> {
    override val descriptor: SerialDescriptor = CallableIdSurrogate.serializer().descriptor

    override fun deserialize(decoder: Decoder): CallableId {
        val surrogate = decoder.decodeSerializableValue(CallableIdSurrogate.serializer())
        return CallableId(surrogate.packageName, surrogate.className, surrogate.callableName)
    }

    override fun serialize(encoder: Encoder, value: CallableId) {
        val surrogate = CallableIdSurrogate(value.packageName, value.className, value.callableName)
        encoder.encodeSerializableValue(CallableIdSurrogate.serializer(), surrogate)
    }
}

/**
 * @property name
 * @property isSpecial
 */
@Serializable
private data class NameSurrogate(
    val name: String,
    val isSpecial: Boolean,
)

/**
 * @property packageFqName
 * @property relativeClassName
 * @property isLocal
 */
@Serializable
private data class ClassIdSurrogate(
    val packageFqName: @Serializable(with = FqNameSerializer::class) FqName,
    val relativeClassName: @Serializable(with = FqNameSerializer::class) FqName,
    val isLocal: Boolean,
)

/**
 * @property packageName
 * @property className
 * @property callableName
 */
@Serializable
private data class CallableIdSurrogate(
    val packageName: @Serializable(with = FqNameSerializer::class) FqName,
    val className: @Serializable(with = FqNameSerializer::class) FqName?,
    val callableName: @Serializable(with = NameSerializer::class) Name,
)
