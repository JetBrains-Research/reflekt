package org.jetbrains.reflekt.plugin.analysis.models.ir

/**
 * @property uses
 * @property instances
 */
data class IrReflektContext(
    var uses: IrReflektUses? = null,
    var instances: IrReflektInstances? = null,
)
