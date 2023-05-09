package org.jetbrains.reflekt.plugin.analysis.models.ir

import kotlinx.serialization.Serializable
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.reflekt.plugin.analysis.models.BaseCollectionReflektData
import org.jetbrains.reflekt.plugin.analysis.serialization.CallableIdSerializer
import org.jetbrains.reflekt.plugin.analysis.serialization.ClassIdSerializer
import org.jetbrains.reflekt.plugin.utils.callableId

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
data class IrInstancesIds(
    val objects: List<@Serializable(with = ClassIdSerializer::class) ClassId> = ArrayList(),
    val classes: List<@Serializable(with = ClassIdSerializer::class) ClassId> = ArrayList(),
    val functions: List<@Serializable(with = CallableIdSerializer::class) CallableId> = ArrayList(),
) {
    fun merge(second: IrInstancesIds) = IrInstancesIds(
        classes = classes.plus(second.classes),
        objects = objects.plus(second.objects),
        functions = functions.plus(second.functions),
    )

    companion object {
        /**
         * Converts [IrInstances] into [IrInstancesIds].
         * For each IrElement, e.g., IrClass or IrFunction, collects its fqName if it is possible.
         *
         * @param irInstances
         * @return [IrInstancesIds]
         */
        fun fromIrInstances(irInstances: IrInstances) = IrInstancesIds(
            classes = irInstances.classes.mapNotNull { it.classId },
            objects = irInstances.objects.mapNotNull { it.classId },
            functions = irInstances.functions.map { it.callableId },
        )
    }
}
