package org.jetbrains.reflekt.plugin.analysis.serialization

import org.jetbrains.reflekt.plugin.analysis.models.SerializableIrType
import org.jetbrains.reflekt.plugin.analysis.models.SerializableIrTypeArgument
import org.jetbrains.reflekt.plugin.analysis.models.ir.LibraryArgumentsWithInstances
import org.jetbrains.reflekt.plugin.analysis.models.ir.SerializableLibraryArgumentsWithInstances

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.types.impl.*
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.types.Variance

import kotlinx.serialization.*
import kotlinx.serialization.protobuf.ProtoBuf

@Suppress("UnnecessaryOptInAnnotation")
@OptIn(ExperimentalSerializationApi::class)
object SerializationUtils {
    private val protoBuf = ProtoBuf

    fun encodeArguments(libraryArgumentsWithInstances: LibraryArgumentsWithInstances): ByteArray =
        protoBuf.encodeToByteArray(libraryArgumentsWithInstances.toSerializableLibraryArgumentsWithInstances())

    fun decodeArguments(byteArray: ByteArray, pluginContext: IrPluginContext): LibraryArgumentsWithInstances {
        val decoded = protoBuf.decodeFromByteArray<SerializableLibraryArgumentsWithInstances>(byteArray)
        return decoded.toLibraryArgumentsWithInstances(pluginContext)
    }

    private fun IrTypeArgument.toSerializableIrTypeArgument(): SerializableIrTypeArgument {
        if (this is IrStarProjection) {
            return SerializableIrTypeArgument(
                isStarProjection = true,
                variance = Variance.INVARIANT,
            )
        }
        val fqName = typeOrNull?.classFqName?.asString() ?: error("Can not get class fq name for IrTypeProjection")
        return SerializableIrTypeArgument(
            fqName = fqName,
            isStarProjection = false,
            variance = (this as IrTypeProjection).variance,
        )
    }

    fun IrType.toSerializableIrType(): SerializableIrType {
        (this as? IrSimpleType) ?: error("Can not cast IrType to IrSimpleType")
        return this.toSerializableIrType()
    }

    @OptIn(ObsoleteDescriptorBasedAPI::class)
    fun IrSimpleType.toSerializableIrType(): SerializableIrType {
        val classifierFqName = this.classifier.descriptor.fqNameOrNull()?.asString() ?: error("Can not get class fq name for ClassifierDescriptor")
        val arguments = this.arguments.map { it.toSerializableIrTypeArgument() }
        // TODO: should we serialize it?
        val abbreviation = null
        return SerializableIrType(
            classifierFqName = classifierFqName,
            nullability = nullability,
            arguments = arguments,
            // We use serialization only for functions signatures, they don't have annotations
            annotations = emptyList(),
            abbreviation = abbreviation,
            variance = variance
        )
    }

    fun SerializableIrType.toIrType(pluginContext: IrPluginContext) = IrSimpleTypeBuilder().also {
        it.classifier = pluginContext.referenceClass(FqName(classifierFqName))
        it.nullability = this.nullability
        it.arguments = this.arguments.map { argument -> argument.toIrTypeArgument(pluginContext) }
        // TODO: should we deserialize it?
        it.abbreviation = null

        it.variance = this.variance
    }.buildSimpleType()

    private fun SerializableIrTypeArgument.toIrTypeArgument(pluginContext: IrPluginContext): IrTypeArgument {
        if (this.isStarProjection) {
            return IrStarProjectionImpl
        }
        requireNotNull(this.fqName) { "Empty fqName for IrTypeProjection" }
        return IrSimpleTypeBuilder().also {
            it.classifier = pluginContext.referenceClass(FqName(this.fqName))
        }.buildTypeProjection()
    }
}
