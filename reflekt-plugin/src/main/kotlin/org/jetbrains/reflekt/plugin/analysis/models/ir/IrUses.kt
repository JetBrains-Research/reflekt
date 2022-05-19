package org.jetbrains.reflekt.plugin.analysis.models.ir

import org.jetbrains.reflekt.plugin.analysis.models.*
import org.jetbrains.reflekt.plugin.analysis.models.psi.*
import org.jetbrains.reflekt.plugin.analysis.processor.FileId
import org.jetbrains.reflekt.plugin.analysis.psi.function.toFunctionInfo

import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.resolve.BindingContext

typealias IrClassOrObjectUses = TypeUses<SupertypesToAnnotations, String>
typealias IrFunctionUses = TypeUses<SignatureToAnnotations, IrFunctionInfo>

/**
 * Stores enough information to generate function reference IR
 *
 * @property fqName
 * @property receiverFqName
 * @property isObjectReceiver
 */
data class IrFunctionInfo(
    val fqName: String,
    val receiverFqName: String?,
    val isObjectReceiver: Boolean,
)

/**
 * @property objects// HashMap<SupertypesToAnnotations, MutableList<String>>
 * @property classes
 * @property functions
 */
data class IrReflektUses(
    override val objects: IrClassOrObjectUses = HashMap(),
    override val classes: IrClassOrObjectUses = HashMap(),
    override val functions: IrFunctionUses = HashMap(),
) : BaseMapReflektData<IrClassOrObjectUses, IrClassOrObjectUses, IrFunctionUses>(
    objects,
    classes,
    functions) {
    @Suppress("VARIABLE_NAME_INCORRECT")
    fun merge(second: IrReflektUses) = merge(this, second) { u1: IrReflektUses, u2: IrReflektUses ->
        IrReflektUses(
            objects = u1.objects.merge(u2.objects) { mutableListOf() },
            classes = u1.classes.merge(u2.classes) { mutableListOf() },
            functions = u1.functions.merge(u2.functions) { mutableListOf() },
        )
    }

    companion object {
        @Suppress("IDENTIFIER_LENGTH")
        fun fromReflektUses(uses: ReflektUses, binding: BindingContext): IrReflektUses {
            if (uses.isEmpty()) {
                return IrReflektUses()
            }
            return IrReflektUses(
                objects = HashMap(uses.objects.flatten().mapValues { (_, v) -> v.map { it.fqName!!.toString() }.toMutableList() }),
                classes = HashMap(uses.classes.flatten().mapValues { (_, v) -> v.map { it.fqName!!.toString() }.toMutableList() }),
                functions = HashMap(uses.functions.flatten().mapValues { (_, v) -> v.map { it.toFunctionInfo(binding) }.toMutableList() }),
            )
        }
    }
}

fun ClassOrObjectUses.toSupertypesToFqNamesMap() = map { (key, value) -> key.supertypes to value.mapNotNull { it.fqName?.toString() } }.toMap()

@Suppress("IDENTIFIER_LENGTH", "TYPE_ALIAS")
fun <T, V : KtElement> HashMap<FileId, TypeUses<T, V>>.flatten(): TypeUses<T, V> {
    val uses: TypeUses<T, V> = HashMap()
    this.forEach { (_, typeUses) ->
        typeUses.forEach { (k, v) ->
            uses.getOrPut(k) { mutableListOf() }.addAll(v)
        }
    }
    return uses
}
