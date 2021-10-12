package io.reflekt.plugin.analysis.serialization

import io.reflekt.plugin.analysis.models.ReflektInvokes
import io.reflekt.plugin.analysis.models.SignatureToAnnotations
import io.reflekt.plugin.analysis.psi.function.toParameterizedType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl
import org.jetbrains.kotlin.descriptors.packageFragments
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.types.KotlinType

@OptIn(ExperimentalSerializationApi::class)
object SerializationUtils {
    private val protoBuf = ProtoBuf

    fun encodeInvokes(invokes: ReflektInvokes): ByteArray =
        protoBuf.encodeToByteArray(ReflektInvokesSerializer, invokes)

    fun decodeInvokes(byteArray: ByteArray, module: ModuleDescriptorImpl): ReflektInvokes {
        val decoded = protoBuf.decodeFromByteArray(ReflektInvokesSerializer, byteArray)
        val functions = decoded.functions.mapValues {
            it.value.map { sa ->
                SignatureToAnnotations(
                    signature = deserializeKotlinType(module, sa.fqName),
                    annotations = sa.annotations,
                    fqName = sa.fqName
                )
            }.toMutableSet()
        } as HashMap

        return ReflektInvokes(
            objects = decoded.objects,
            classes = decoded.classes,
            functions = functions
        )
    }

    private fun deserializeKotlinType(module: ModuleDescriptorImpl, fqName: String?): KotlinType? {
        fqName ?: error("Fq name after deserialization is null")
        val packageFragmentDescriptor = module.packageFragmentProvider.packageFragments(FqName(fqName)).find { it.fqName.asString() == fqName }
            ?: error("Can not find descriptor with fqName $fqName")
        val scope = packageFragmentDescriptor.getMemberScope()
        val name = scope.getFunctionNames().find { it.asString() == fqName } ?: error("Can not find function name $fqName")
        val descriptor = scope.getContributedFunctions(name, NoLookupLocation.WHEN_RESOLVE_DECLARATION)
        return (descriptor as FunctionDescriptor).toParameterizedType()
    }
}
