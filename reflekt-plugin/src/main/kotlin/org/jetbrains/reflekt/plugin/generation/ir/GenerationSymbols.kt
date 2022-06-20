package org.jetbrains.reflekt.plugin.generation.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.jvm.functionByName
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.JvmNames
import org.jetbrains.reflekt.plugin.analysis.common.ReflektTerminalFunction
import org.jetbrains.reflekt.plugin.analysis.common.SmartReflektTerminalFunction
import org.jetbrains.reflekt.plugin.generation.common.*

/**
 * Acquires symbols of standard library and DSL declarations that used for Reflekt's IR transformation.
 */
class GenerationSymbols(private val pluginContext: IrPluginContext) {
    private val irBuiltIns = pluginContext.irBuiltIns
    val anyConstructor = irBuiltIns.anyClass.constructors.single()
    val jvmSyntheticConstructor = pluginContext.referenceConstructors(JvmNames.JVM_SYNTHETIC_ANNOTATION_FQ_NAME).first()
    val jvmFieldConstructor = pluginContext.referenceConstructors(FqName("kotlin.jvm.JvmField")).first()
    val listOf = funCollectionOf("kotlin.collections.listOf")
    val mapGet = irBuiltIns.mapClass.owner.functions.first { it.name.asString() == "get" && it.valueParameters.size == 1 }.symbol
    val setOf = funCollectionOf("kotlin.collections.setOf")
    val hashMapClass = pluginContext.referenceTypeAlias(FqName("kotlin.collections.HashMap"))!!.owner.expandedType
        .classOrNull!!
    val hashMapOf = funCollectionOf("kotlin.collections.hashMapOf")
    val hashSetOf = funCollectionOf("kotlin.collections.hashSetOf")
    private val hashSetClass = pluginContext.referenceTypeAlias(FqName("kotlin.collections.HashSet"))!!.owner.expandedType
        .classOrNull!!
    private val mutableSetClass = pluginContext.referenceClass(FqName("kotlin.collections.MutableSet"))!!
    val mutableSetAdd = mutableSetClass.functionByName("add")
    val hashSetConstructor = hashSetClass.constructors.first { it.owner.valueParameters.isEmpty() }
    val pairClass = pluginContext.referenceClass(FqName("kotlin.Pair"))!!
    val to = pluginContext.referenceFunctions(FqName("kotlin.to")).first()
    val reflektClassClass = pluginContext.referenceClass(FqName("org.jetbrains.reflekt.ReflektClass"))!!
    val reflektClassImplClass = pluginContext.referenceClass(FqName("org.jetbrains.reflekt.ReflektClassImpl"))!!
    val reflektClassImplConstructor = reflektClassImplClass.constructors.first()
    val reflektClassImplGetSealedSubclasses = reflektClassImplClass.getPropertyGetter("sealedSubclasses")!!
    val reflektClassImplGetSuperclasses = reflektClassImplClass.getPropertyGetter("superclasses")!!
    val reflektVisibilityClass = pluginContext.referenceClass(FqName("org.jetbrains.reflekt.ReflektVisibility"))!!

    private fun funCollectionOf(fqName: String) = pluginContext.referenceFunctions(FqName(fqName)).first {
        val parameters = it.owner.valueParameters
        parameters.size == 1 && parameters[0].isVararg
    }

    /**
     * Generates IR for the Reflekt terminal function (toList/toSet/etc).
     *
     * @param parts
     */
    fun irTerminalFunction(parts: BaseReflektInvokeParts) = when (parts) {
        is ReflektInvokeParts -> when (parts.terminalFunction) {
            ReflektTerminalFunction.TO_LIST -> listOf
            ReflektTerminalFunction.TO_SET -> setOf
        }

        is SmartReflektInvokeParts -> when (parts.terminalFunction) {
            SmartReflektTerminalFunction.RESOLVE -> listOf
        }
    }
}
