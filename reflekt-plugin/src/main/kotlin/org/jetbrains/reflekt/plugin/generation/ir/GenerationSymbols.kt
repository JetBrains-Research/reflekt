package org.jetbrains.reflekt.plugin.generation.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.ir.createImplicitParameterDeclarationWithWrappedDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.IrBuiltIns
import org.jetbrains.kotlin.ir.builders.declarations.addFunction
import org.jetbrains.kotlin.ir.builders.declarations.addTypeParameter
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildClass
import org.jetbrains.kotlin.ir.declarations.IrFactory
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrPackageFragment
import org.jetbrains.kotlin.ir.declarations.impl.IrExternalPackageFragmentImpl
import org.jetbrains.kotlin.ir.declarations.impl.IrFactoryImpl
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.makeNullable
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.isVararg
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.Variance

class GenerationSymbols(private val pluginContext: IrPluginContext) {
    private val irBuiltIns = pluginContext.irBuiltIns

    val mapGet = irBuiltIns.mapClass.owner.functions
        .single { it.name.asString() == "get" && it.valueParameters.size == 1 }

    private fun funCollectionOf(fqName: String) = pluginContext.referenceFunctions(FqName(fqName)).single {
        val parameters = it.owner.valueParameters
        parameters.size == 1 && parameters[0].isVararg
    }

    val arrayListOf = funCollectionOf("kotlin.collections.arrayListOf")
    val hashSetOf = funCollectionOf("kotlin.collections.hashSetOf")
    val hashMapOf = funCollectionOf("kotlin.collections.hashMapOf")
    val to = pluginContext.referenceFunctions(FqName("kotlin.to")).single()
}
