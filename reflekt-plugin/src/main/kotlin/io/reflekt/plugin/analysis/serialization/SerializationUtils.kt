package io.reflekt.plugin.analysis.serialization

import io.reflekt.plugin.analysis.models.*
import kotlinx.serialization.*
import kotlinx.serialization.protobuf.ProtoBuf
import org.jetbrains.kotlin.builtins.DefaultBuiltIns
import org.jetbrains.kotlin.builtins.createFunctionType
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.lazy.ResolveSessionUtils.getClassDescriptorsByFqName
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeProjection

@OptIn(ExperimentalSerializationApi::class)
object SerializationUtils {
    private val protoBuf = ProtoBuf

    fun encodeInvokes(invokesWithPackages: ReflektInvokesWithPackages): ByteArray {
        return protoBuf.encodeToByteArray(invokesWithPackages.toSerializableReflektInvokesWithPackages())
    }

    fun decodeInvokes(byteArray: ByteArray, module: ModuleDescriptorImpl): ReflektInvokesWithPackages {
        val decoded = protoBuf.decodeFromByteArray<SerializableReflektInvokesWithPackages>(byteArray)
        return decoded.toReflektInvokesWithPackages(module)
    }

    private fun deserializeKotlinType(module: ModuleDescriptorImpl, fqName: String?): KotlinType {
        fqName ?: error("Fq name after deserialization is null")
        val classDescriptor = getClassDescriptorsByFqName(module, FqName(fqName)).find { it.fqNameSafe.asString() == fqName }
            ?: error("Can not find class descriptor with fqName $fqName")
        return classDescriptor.defaultType
    }

    fun SerializableKotlinType.toKotlinType(module: ModuleDescriptorImpl): KotlinType {
        val args = arguments.map { deserializeKotlinType(module, it.fqName) }
        val returnType = deserializeKotlinType(module, this.returnType)
        // TODO: get receiverType
        val receiverType: KotlinType? = null
        return createFunctionType(
            DefaultBuiltIns.Instance,
            Annotations.EMPTY,
            receiverType,
            parameterTypes = args,
            returnType = returnType,
            suspendFunction = false,
            parameterNames = null
        )
    }

    private fun TypeProjection.toSerializableTypeProjection() =
        SerializableTypeProjection(
            fqName = type.fullFqName(),
            isStarProjection = isStarProjection,
            projectionKind = projectionKind
        )

    fun KotlinType.toSerializableKotlinType(): SerializableKotlinType {
        val returnType = arguments.last().type.fullFqName()
        return SerializableKotlinType(
            fqName = fullFqName(),
            arguments = arguments.dropLast(1).map { it.toSerializableTypeProjection() },
            returnType = returnType
        )
    }

    private fun KotlinType.fullFqName(): String {
        val declaration = requireNotNull(this.constructor.declarationDescriptor) {
            "declarationDescriptor is null for constructor = $this.constructor with ${this.constructor.javaClass}"
        }
        return DescriptorUtils.getFqName(declaration).asString()
    }
}
