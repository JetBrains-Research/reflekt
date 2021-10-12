package io.reflekt.plugin.analysis.serialization

import io.reflekt.plugin.analysis.models.*
import io.reflekt.plugin.analysis.processor.FileID
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.serializer

object ReflektInvokesSerializer : KSerializer<ReflektInvokes> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ReflektInvokes") {
        element<HashMap<FileID, ClassOrObjectInvokes>>("objects")
        element<HashMap<FileID, ClassOrObjectInvokes>>("classes")
        element<HashMap<FileID, FunctionInvokes>>("functions")
    }

    override fun deserialize(decoder: Decoder): ReflektInvokes {
        return decoder.decodeStructure(descriptor) {
            var objects: HashMap<FileID, ClassOrObjectInvokes>? = null
            var classes: HashMap<FileID, ClassOrObjectInvokes>? = null
            var functions: HashMap<FileID, FunctionInvokes>? = null

            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    ElementIndex.OBJECTS -> objects = decodeSerializableElement(descriptor, index, serializer())
                    ElementIndex.CLASSES -> classes = decodeSerializableElement(descriptor, index, serializer())
                    ElementIndex.FUNCTIONS -> functions = decodeSerializableElement(descriptor, index, serializer())
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            if (objects == null || classes == null || functions == null) {
                error("Serialization error: ReflektInvokesSerializer")
            }
            ReflektInvokes(
                objects = objects,
                classes = classes,
                functions = functions
            )
        }
    }

    override fun serialize(encoder: Encoder, value: ReflektInvokes) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, ElementIndex.OBJECTS, serializer(), value.objects)
            encodeSerializableElement(descriptor, ElementIndex.CLASSES, serializer(), value.classes)
            encodeSerializableElement(descriptor, ElementIndex.FUNCTIONS, serializer(), value.functions)
        }
    }

    private object ElementIndex {
        const val OBJECTS = 0
        const val CLASSES = 1
        const val FUNCTIONS = 2
    }
}
