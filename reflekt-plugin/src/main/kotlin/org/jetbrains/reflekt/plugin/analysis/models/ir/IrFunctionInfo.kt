package org.jetbrains.reflekt.plugin.analysis.models.ir

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
