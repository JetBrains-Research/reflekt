package io.reflekt.plugin.analysis.serialization

import io.reflekt.plugin.analysis.models.SignatureToAnnotations
import io.reflekt.plugin.analysis.psi.function.toParameterizedType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.serializer
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl
import org.jetbrains.kotlin.descriptors.packageFragments
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.KotlinType

object SignatureToAnnotationsSerializer : KSerializer<SignatureToAnnotations> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("SignatureToAnnotations") {
        element<KotlinType>("signature")
        element<Set<String>>("annotations")
        element<String?>("fqName")
    }

    override fun deserialize(decoder: Decoder): SignatureToAnnotations {
        return decoder.decodeStructure(descriptor) {
            var annotations: Set<String>? = null
            var fqName: String? = null
            val signature: KotlinType? = null

            while (true) {
                when (val index = decodeElementIndex(ReflektInvokesSerializer.descriptor)) {
                    ElementIndex.SIGNATURE -> continue
                    ElementIndex.ANNOTATIONS -> annotations = decodeSerializableElement(descriptor, index, serializer())
                    ElementIndex.FQ_NAME -> fqName = decodeSerializableElement(descriptor, index, serializer())
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            fqName ?: error("Serialization error, fqName is null: SignatureToAnnotationsSerializer")
//            val packageFragmentDescriptor = module.packageFragmentProvider.packageFragments(FqName(fqName)).find { it.fqName.asString() == fqName }
//                ?: error("Can not find descriptor with fqName $fqName")
//            val scope = packageFragmentDescriptor.getMemberScope()
//            val name = scope.getFunctionNames().find { it.asString() == fqName } ?: error("Can not find function name $fqName")
//            val descriptor = scope.getContributedFunctions(name, NoLookupLocation.WHEN_RESOLVE_DECLARATION)
//            val signature = (descriptor as FunctionDescriptor).toParameterizedType()

            // In this case we can not have fqName == null
            if ( annotations == null) {
                error("Serialization error: SignatureToAnnotationsSerializer")
            }
            SignatureToAnnotations(
                signature = signature,
                annotations = annotations,
                fqName = fqName
            )
        }
    }

    override fun serialize(encoder: Encoder, value: SignatureToAnnotations) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, ElementIndex.SIGNATURE, serializer(), "")
            encodeSerializableElement(descriptor, ElementIndex.ANNOTATIONS, serializer(), value.annotations)
            encodeSerializableElement(descriptor, ElementIndex.FQ_NAME, serializer(), value.fqName)
        }
    }

    private object ElementIndex {
        const val SIGNATURE = 0
        const val ANNOTATIONS = 1
        const val FQ_NAME = 2
    }
}
