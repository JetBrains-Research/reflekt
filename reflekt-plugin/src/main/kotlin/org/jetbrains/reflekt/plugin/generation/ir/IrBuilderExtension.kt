package org.jetbrains.reflekt.plugin.generation.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.impl.IrAnonymousInitializerSymbolImpl
import org.jetbrains.kotlin.ir.types.makeNotNull
import org.jetbrains.reflekt.plugin.generation.ir.util.irCall

interface IrBuilderExtension {
    val pluginContext: IrPluginContext

    val irBuiltIns
        get() = pluginContext.irBuiltIns

    @OptIn(ObsoleteDescriptorBasedAPI::class)
    fun IrClass.contributeAnonymousInitializer(body: IrBlockBodyBuilder.() -> Unit) {
        factory.createAnonymousInitializer(startOffset, endOffset, origin, IrAnonymousInitializerSymbolImpl(descriptor)).also {
            it.parent = this
            declarations += it
            it.body = DeclarationIrBuilder(pluginContext, it.symbol, startOffset, endOffset).irBlockBody(startOffset, endOffset, body)
        }
    }

    fun IrBuilderWithScope.irCheckNotNull(value: IrExpression) = irCall(
        irBuiltIns.checkNotNullSymbol,
        typeArguments = listOf(value.type.makeNotNull()),
        valueArguments = listOf(value),
    )
}
