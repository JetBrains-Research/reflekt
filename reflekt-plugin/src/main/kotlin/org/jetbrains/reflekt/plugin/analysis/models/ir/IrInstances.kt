package org.jetbrains.reflekt.plugin.analysis.models.ir

import org.jetbrains.reflekt.plugin.analysis.models.BaseCollectionReflektData

import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable

import kotlinx.serialization.Serializable

/**
 * Stores all [classes], [objects], and [functions] from the project.
 *
 * @property objects
 * @property classes
 * @property functions
 */
data class IrInstances(
    override val objects: List<IrClass> = ArrayList(),
    override val classes: List<IrClass> = ArrayList(),
    override val functions: List<IrFunction> = ArrayList(),
) : BaseCollectionReflektData<List<IrClass>, List<IrClass>, List<IrFunction>>(
    objects,
    classes,
    functions,
)

/**
 * Stores all [classes], [objects], and [functions] fq names from the project for the ReflektMeta file.
 * @property objects
 * @property classes
 * @property functions
 */
// TODO: We can not inherit from BaseCollectionReflektData since this issue:
// https://github.com/Kotlin/kotlinx.serialization/issues/1264
@Serializable
data class IrInstancesFqNames(
    val objects: List<String> = ArrayList(),
    val classes: List<String> = ArrayList(),
    val functions: List<String> = ArrayList(),
) {
    fun merge(second: IrInstancesFqNames) = IrInstancesFqNames(
        classes = classes.plus(second.classes),
        objects = objects.plus(second.objects),
        functions = functions.plus(second.functions),
    )

    companion object {
        /**
         * Converts [IrInstances] into [IrInstancesFqNames].
         * For each IrElement, e.g. IrClass or IrFunction, collects its fqName if it is possible.
         *
         * @param irInstances
         * @return [IrInstancesFqNames]
         */
        fun fromIrInstances(irInstances: IrInstances) = IrInstancesFqNames(
            classes = irInstances.classes.fqNameWhenAvailable(),
            objects = irInstances.objects.fqNameWhenAvailable(),
            functions = irInstances.functions.fqNameWhenAvailable(),
        )

        private fun <T : IrDeclarationWithName> List<T>.fqNameWhenAvailable() = this.mapNotNull { it.fqNameWhenAvailable?.asString() }
    }
}
