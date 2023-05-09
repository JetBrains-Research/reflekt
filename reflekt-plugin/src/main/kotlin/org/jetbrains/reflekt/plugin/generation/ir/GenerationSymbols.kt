package org.jetbrains.reflekt.plugin.generation.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.jvm.functionByName
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.*
import org.jetbrains.reflekt.plugin.analysis.common.*
import org.jetbrains.reflekt.plugin.generation.common.*

/**
 * Acquires symbols of standard library and DSL declarations that used for Reflekt's IR transformation.
 */
class GenerationSymbols(private val pluginContext: IrPluginContext) {
    private val irBuiltIns = pluginContext.irBuiltIns
    val anyConstructor = irBuiltIns.anyClass.constructors.single()
    val jvmSyntheticConstructor = referenceConstructorsOrFail(ClassId(StandardClassIds.BASE_JVM_PACKAGE, Name.identifier("JvmSynthetic"))).first()
    val jvmFieldConstructor = referenceConstructorsOrFail(ClassId(StandardClassIds.BASE_JVM_PACKAGE, Name.identifier("JvmField"))).first()
    val listOf = referenceVarargCollectionFunction("listOf")
    val mapGet = irBuiltIns
        .mapClass
        .owner
        .functions
        .first { it.name.asString() == "get" && it.valueParameters.size == 1 }
        .symbol
    val setOf = referenceVarargCollectionFunction("setOf")
    val hashMapClass = referenceTypeAliasOrFail(ClassId(StandardClassIds.BASE_COLLECTIONS_PACKAGE, Name.identifier("HashMap"))).owner.expandedType.classOrNull!!
    val hashMapOf = referenceVarargCollectionFunction("hashMapOf")
    val hashSetOf = referenceVarargCollectionFunction("hashSetOf")
    private val hashSetClass =
        referenceTypeAliasOrFail(ClassId(StandardClassIds.BASE_COLLECTIONS_PACKAGE, Name.identifier("HashSet"))).owner.expandedType.classOrNull!!
    val mutableSetAdd = irBuiltIns.mutableSetClass.functionByName("add")
    val hashSetConstructor = hashSetClass.constructors.first { it.owner.valueParameters.isEmpty() }
    val pairClass = referenceClassOrFail(ClassId(StandardClassIds.BASE_KOTLIN_PACKAGE, Name.identifier("Pair")))
    val to = checkNotNull(
        pluginContext.referenceFunctions(CallableId(StandardClassIds.BASE_KOTLIN_PACKAGE, Name.identifier("to"))).takeIf { it.isNotEmpty() },
    ) {
        "Can't reference functions named ${"kotlin.to"}, Kotlin standard library or Reflekt DSL aren't available"
    }.first()
    val reflektClassClass = referenceClassOrFail(ReflektNames.REFLEKT_CLASS_CLASS_ID)
    val reflektClassImplClass = referenceClassOrFail(ReflektNames.REFLEKT_CLASS_IMPL_CLASS_ID)
    val reflektClassImplConstructor = reflektClassImplClass.constructors.first()
    val reflektClassImplGetSealedSubclasses = reflektClassImplClass.getPropertyGetter("sealedSubclasses")!!
    val reflektClassImplGetSuperclasses = reflektClassImplClass.getPropertyGetter("superclasses")!!
    val reflektObjectClass = referenceClassOrFail(ReflektNames.REFLEKT_OBJECT_CLASS_ID)
    val reflektObjectConstructor = reflektObjectClass.constructors.first()
    val reflektVisibilityClass = referenceClassOrFail(ReflektNames.REFLEKT_VISIBILITY_CLASS_ID)

    private fun referenceVarargCollectionFunction(shortName: String) =
        pluginContext.referenceFunctions(CallableId(StandardClassIds.BASE_COLLECTIONS_PACKAGE, Name.identifier(shortName))).firstOrNull {
            val parameters = it.owner.valueParameters
            parameters.size == 1 && parameters[0].isVararg
        } ?: error("Can't reference function $shortName, Kotlin standard library or Reflekt DSL aren't available")

    /**
     * Generates IR for the Reflekt terminal function ([toList]/[toSet]/etc).
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

    private fun referenceClassOrFail(classID: ClassId) = checkNotNull(pluginContext.referenceClass(classID)) {
        "Can't reference class $classID, Kotlin standard library or Reflekt DSL aren't available"
    }

    private fun referenceTypeAliasOrFail(classID: ClassId) = checkNotNull(pluginContext.referenceTypeAlias(classID)) {
        "Can't reference class $classID, Kotlin standard library or Reflekt DSL aren't available"
    }

    private fun referenceConstructorsOrFail(classID: ClassId) =
        checkNotNull(pluginContext.referenceConstructors(classID).takeIf { it.isNotEmpty() }) {
            "Can't reference constructors of $classID, Kotlin standard library or Reflekt DSL aren't available"
        }
}
