package org.jetbrains.reflekt.plugin.analysis.models.ir

import org.jetbrains.reflekt.plugin.analysis.models.BaseCollectionReflektData

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction

/**
 * Stores all [classes], [objects], and [functions] from the project
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
