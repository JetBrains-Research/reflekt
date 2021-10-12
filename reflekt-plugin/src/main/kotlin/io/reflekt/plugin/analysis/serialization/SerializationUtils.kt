package io.reflekt.plugin.analysis.serialization

import io.reflekt.plugin.analysis.models.ReflektInvokes
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf

@OptIn(ExperimentalSerializationApi::class)
object SerializationUtils {
    private val protoBuf = ProtoBuf

    fun encodeInvokes(invokes: ReflektInvokes): ByteArray =
        protoBuf.encodeToByteArray(ReflektInvokesSerializer, invokes)

    fun decodeInvokes(byteArray: ByteArray): ReflektInvokes =
        protoBuf.decodeFromByteArray(ReflektInvokesSerializer, byteArray)
}
