package io.reflekt.plugin.analysis.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.jetbrains.kotlin.types.KotlinType

object KotlinTypeSerializer : KSerializer<KotlinType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("KotlinType", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): KotlinType =
        decoder.decodeString().deserialize()

    override fun serialize(encoder: Encoder, value: KotlinType) =
        encoder.encodeString(value.serialize())

    private fun KotlinType.serialize(): String {
        TODO()
    }

    private fun String.deserialize(): KotlinType {
        TODO()
    }
}
