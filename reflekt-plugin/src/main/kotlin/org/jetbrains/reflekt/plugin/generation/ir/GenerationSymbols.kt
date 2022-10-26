package org.jetbrains.reflekt.plugin.generation.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.jvm.functionByName
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.JvmNames
import org.jetbrains.reflekt.plugin.analysis.common.*
import org.jetbrains.reflekt.plugin.generation.common.*

/**
 * Acquires symbols of standard library and DSL declarations that used for Reflekt's IR transformation.
 */
class GenerationSymbols(private val pluginContext: IrPluginContext) {
    private val irBuiltIns = pluginContext.irBuiltIns
    val anyConstructor = irBuiltIns.anyClass.constructors.single()
    val jvmSyntheticConstructor = referenceConstructorsOrFail(JvmNames.JVM_SYNTHETIC_ANNOTATION_FQ_NAME).first()
    val jvmFieldConstructor = referenceConstructorsOrFail(FqName("kotlin.jvm.JvmField")).first()
    val listOf = referenceVarargCollectionFunction("kotlin.collections.listOf")
    val mapGet = irBuiltIns.mapClass.owner.functions.first { it.name.asString() == "get" && it.valueParameters.size == 1 }.symbol
    val setOf = referenceVarargCollectionFunction("kotlin.collections.setOf")
    val hashMapClass = referenceTypeAliasOrFail("kotlin.collections.HashMap").owner.expandedType.classOrNull!!
    val hashMapOf = referenceVarargCollectionFunction("kotlin.collections.hashMapOf")
    val hashSetOf = referenceVarargCollectionFunction("kotlin.collections.hashSetOf")
    private val hashSetClass = referenceTypeAliasOrFail("kotlin.collections.HashSet").owner.expandedType.classOrNull!!
    val mutableSetAdd = irBuiltIns.mutableSetClass.functionByName("add")
    val hashSetConstructor = hashSetClass.constructors.first { it.owner.valueParameters.isEmpty() }
    val pairClass = referenceClassOrFail("kotlin.Pair")
    val to = checkNotNull(pluginContext.referenceFunctions(FqName("kotlin.to")).takeIf { it.isNotEmpty() }) {
        "Can't reference function kotlin.to, because Kotlin standard library or Reflekt DSL aren't available"
    }.first()
    val reflektClassClass = referenceClassOrFail("${ReflektPackage.PACKAGE_NAME}.ReflektClass")
    val reflektClassImplClass = referenceClassOrFail("${ReflektPackage.PACKAGE_NAME}.ReflektClassImpl")
    val reflektClassImplConstructor = reflektClassImplClass.constructors.first()
    val reflektClassImplGetSealedSubclasses = reflektClassImplClass.getPropertyGetter("sealedSubclasses")!!
    val reflektClassImplGetSuperclasses = reflektClassImplClass.getPropertyGetter("superclasses")!!
    val reflektObjectClass = referenceClassOrFail("${ReflektPackage.PACKAGE_NAME}.ReflektObject")
    val reflektObjectConstructor = reflektObjectClass.constructors.first()
    val reflektVisibilityClass = referenceClassOrFail("${ReflektPackage.PACKAGE_NAME}.ReflektVisibility")

    private fun referenceVarargCollectionFunction(fqName: String) = pluginContext.referenceFunctions(FqName(fqName)).firstOrNull {
        val parameters = it.owner.valueParameters
        parameters.size == 1 && parameters[0].isVararg
    } ?: error("Can't reference function $fqName, Kotlin standard library or Reflekt DSL aren't available")

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

    private fun referenceClassOrFail(fqNameString: String) = checkNotNull(pluginContext.referenceClass(FqName(fqNameString))) {
        "Can't reference class $fqNameString, because Kotlin standard library or Reflekt DSL aren't available"
    }

    private fun referenceTypeAliasOrFail(fqNameString: String) = checkNotNull(pluginContext.referenceTypeAlias(FqName(fqNameString))) {
        "Can't reference class $fqNameString, because Kotlin standard library or Reflekt DSL aren't available"
    }

    private fun referenceConstructorsOrFail(fqName: FqName) = checkNotNull(pluginContext.referenceConstructors(fqName).takeIf { it.isNotEmpty() }) {
        "Can't reference constructors of $fqName, because Kotlin standard library or Reflekt DSL aren't available"
    }
}
