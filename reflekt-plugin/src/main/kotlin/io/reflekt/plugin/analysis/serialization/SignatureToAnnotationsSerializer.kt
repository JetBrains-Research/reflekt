package io.reflekt.plugin.analysis.serialization

import io.reflekt.plugin.analysis.models.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.serializer
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeProjection

object SignatureToAnnotationsSerializer : KSerializer<SignatureToAnnotations> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("SignatureToAnnotations") {
        element<SerializableKotlinType>("signature")
        element<Set<String>>("annotations")
    }

    override fun deserialize(decoder: Decoder): SignatureToAnnotations {
        return decoder.decodeStructure(descriptor) {
            var annotations: Set<String>? = null
            var serializableKotlinType: SerializableKotlinType? = null
            var signature: KotlinType? = null

            while (true) {
                when (val index = decodeElementIndex(ReflektInvokesSerializer.descriptor)) {
                    ElementIndex.SIGNATURE -> serializableKotlinType = decodeSerializableElement(descriptor, index, serializer())
                    ElementIndex.ANNOTATIONS -> annotations = decodeSerializableElement(descriptor, index, serializer())
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            serializableKotlinType ?: error("Serialization error, serializableKotlinType is null: SignatureToAnnotationsSerializer")

            // TODO: deserialize serializableKotlinType
            // In this case we can not have fqName == null
            if ( annotations == null) {
                error("Serialization error: SignatureToAnnotationsSerializer")
            }
            SignatureToAnnotations(
                signature = signature,
                annotations = annotations,
            )
        }
    }

    override fun serialize(encoder: Encoder, value: SignatureToAnnotations) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, ElementIndex.SIGNATURE, serializer(), value.signature!!.toSerializableSerializableKotlinType())
            encodeSerializableElement(descriptor, ElementIndex.ANNOTATIONS, serializer(), value.annotations)
        }
    }

    private object ElementIndex {
        const val SIGNATURE = 0
        const val ANNOTATIONS = 1
    }

    private fun TypeProjection.toSerializableTypeProjection() =
        SerializableTypeProjection(
            fqName = type.fullFqName(),
            isStarProjection = isStarProjection,
            projectionKind = projectionKind
        )

    private fun KotlinType.toSerializableSerializableKotlinType() =
        SerializableKotlinType(
            fqName = fullFqName(),
            arguments = arguments.map { it.toSerializableTypeProjection() }
        )

    private fun KotlinType.fullFqName(): String {
        val declaration = requireNotNull(this.constructor.declarationDescriptor) {
            "declarationDescriptor is null for constructor = $this.constructor with ${this.constructor.javaClass}"
        }
        return DescriptorUtils.getFqName(declaration).asString()
    }
}
