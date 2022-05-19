package org.jetbrains.reflekt.plugin.analysis.serialization

import org.jetbrains.reflekt.plugin.analysis.models.SerializableKotlinType
import org.jetbrains.reflekt.plugin.analysis.models.SerializableTypeProjection
import org.jetbrains.reflekt.plugin.analysis.models.psi.*

import org.jetbrains.kotlin.builtins.*
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.lazy.ResolveSessionUtils.getClassDescriptorsByFqName
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeProjection

import kotlinx.serialization.*
import kotlinx.serialization.protobuf.ProtoBuf

@OptIn(ExperimentalSerializationApi::class)
object SerializationUtils {
    private val protoBuf = ProtoBuf

    fun encodeInvokes(invokesWithPackages: ReflektInvokesWithPackages): ByteArray = protoBuf.encodeToByteArray(invokesWithPackages.toSerializableReflektInvokesWithPackages())

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
        return createFunctionType(
            DefaultBuiltIns.Instance,
            Annotations.EMPTY,
            this.receiverType?.toKotlinType(module),
            parameterTypes = args,
            returnType = returnType,
            suspendFunction = false,
            parameterNames = null,
            contextReceiverTypes = contextReceiverTypes.map { it.toKotlinType(module) },
        )
    }

    private fun TypeProjection.toSerializableTypeProjection() =
        SerializableTypeProjection(
            fqName = type.fullFqName(),
            isStarProjection = isStarProjection,
            projectionKind = projectionKind,
        )

    fun KotlinType.toSerializableKotlinType(): SerializableKotlinType {
        val returnType = arguments.last().type.fullFqName()
        val receiverType = this.getReceiverTypeFromFunctionType()?.toSerializableKotlinType()
        val contextReceiverTypes = getContextReceiverTypesFromFunctionType().map { it.toSerializableKotlinType() }
        return SerializableKotlinType(
            fqName = fullFqName(),
            arguments = arguments.dropLast(1).map { it.toSerializableTypeProjection() },
            returnType = returnType,
            receiverType = receiverType,
            contextReceiverTypes = contextReceiverTypes,
        )
    }

    private fun KotlinType.fullFqName(): String {
        val declaration = requireNotNull(this.constructor.declarationDescriptor) {
            "declarationDescriptor is null for constructor = $this.constructor with ${this.constructor.javaClass}"
        }
        return DescriptorUtils.getFqName(declaration).asString()
    }
}
