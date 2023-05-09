package org.jetbrains.reflekt.plugin.analysis.models.ir

import org.jetbrains.kotlin.name.CallableId

/**
 * Stores enough information to generate function reference IR
 *
 * @property callableId The callable ID of function.
 * @property isObjectReceiver
 */
data class IrFunctionInfo(
    val callableId: CallableId,
    val isObjectReceiver: Boolean,
)
