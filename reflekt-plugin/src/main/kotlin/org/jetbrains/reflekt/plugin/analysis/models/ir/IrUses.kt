package org.jetbrains.reflekt.plugin.analysis.models.ir

import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.reflekt.plugin.analysis.models.*
import org.jetbrains.reflekt.plugin.analysis.processor.FileID
import org.jetbrains.reflekt.plugin.analysis.psi.function.toFunctionInfo

fun <T, V : KtElement> HashMap<FileID, TypeUses<T, V>>.flatten(): TypeUses<T, V> {
    val uses: TypeUses<T, V> = HashMap()
    this.forEach { (_, typeUses) ->
        typeUses.forEach { (k, v) ->
            uses.getOrPut(k) { mutableListOf() }.addAll(v)
        }
    }
    return uses
}

/* Stores enough information to generate function reference IR */
data class IrFunctionInfo(
    val fqName: String,
    val receiverFqName: String?,
    val isObjectReceiver: Boolean
)

typealias IrClassOrObjectUses = TypeUses<SupertypesToAnnotations, String>
typealias IrFunctionUses = TypeUses<SignatureToAnnotations, IrFunctionInfo>

fun ClassOrObjectUses.toSupertypesToFqNamesMap(): Map<Set<String>, List<String>> {
    return this.map { it.key.supertypes to it.value.mapNotNull { it.fqName?.toString() } }.toMap()
}

data class IrReflektUses(
    // HashMap<SupertypesToAnnotations, MutableList<String>>
    override val objects: IrClassOrObjectUses = HashMap(),
    override val classes: IrClassOrObjectUses = HashMap(),
    override val functions: IrFunctionUses = HashMap()
) : BaseMapReflektData<IrClassOrObjectUses, IrClassOrObjectUses, IrFunctionUses>(objects, classes, functions) {
    companion object {
        fun fromReflektUses(uses: ReflektUses, binding: BindingContext): IrReflektUses {
            if (uses.isEmpty()) return IrReflektUses()
            return IrReflektUses(
                objects = HashMap(uses.objects.flatten().mapValues { (_, v) -> v.map { it.fqName!!.toString() }.toMutableList() }),
                classes = HashMap(uses.classes.flatten().mapValues { (_, v) -> v.map { it.fqName!!.toString() }.toMutableList() }),
                functions = HashMap(uses.functions.flatten().mapValues { (_, v) -> v.map { it.toFunctionInfo(binding) }.toMutableList() }),
            )
        }
    }

    fun merge(second: IrReflektUses) = merge(this, second) { u1: IrReflektUses, u2: IrReflektUses ->
        IrReflektUses(
            objects = u1.objects.merge(u2.objects),
            classes = u1.classes.merge(u2.classes),
            functions = u1.functions.merge(u2.functions)
        )
    }
}
