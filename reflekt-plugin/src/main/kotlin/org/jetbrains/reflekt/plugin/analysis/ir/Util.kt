@file:OptIn(ObsoleteDescriptorBasedAPI::class)

package org.jetbrains.reflekt.plugin.analysis.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.types.impl.makeTypeProjection
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.reflekt.plugin.analysis.models.ir.IrFunctionInfo
import org.jetbrains.reflekt.plugin.utils.callableId

fun IrCall.getClassIdsOfTypeArguments(): List<ClassId> {
    val result = ArrayList<ClassId>()
    for (i in 0 until typeArgumentsCount) {
        val type = getTypeArgument(i)
        require(type is IrSimpleType) { "Type argument is not IrSimpleType" }
        result += type.classOrNull?.owner?.classId ?: continue
    }
    return result
}

fun IrCall.getClassIdsOfClassReferenceValueArguments(): List<ClassId> =
    (getValueArgument(0) as? IrVararg)?.elements?.mapNotNull {
        (it as IrClassReference).classType.classOrNull?.owner
            ?.classId
    } ?: emptyList()

@OptIn(ObsoleteDescriptorBasedAPI::class)
fun IrType.toParameterizedType() = toKotlinType()

fun IrClass.isSubtypeOf(type: IrType, pluginContext: IrPluginContext) = this.defaultType.isSubtypeOf(type, IrTypeSystemContextImpl(pluginContext.irBuiltIns))

fun IrType.makeTypeProjection() = makeTypeProjection(this, if (this is IrTypeProjection) this.variance else Variance.INVARIANT)

fun IrFunction.toFunctionInfo(): IrFunctionInfo = IrFunctionInfo(
    callableId,
    isObjectReceiver = receiverType()?.getClass()?.isObject ?: false,
)

fun IrFunction.receiverType(): IrType? = extensionReceiverParameter?.type ?: dispatchReceiverParameter?.type
