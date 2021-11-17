package org.jetbrains.reflekt.plugin.analysis.models.ir

data class ReflektContext(
    var uses: IrReflektUses? = null,
    var instances: IrReflektInstances? = null
)
