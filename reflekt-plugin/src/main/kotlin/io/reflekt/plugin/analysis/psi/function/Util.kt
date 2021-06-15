package io.reflekt.plugin.analysis.psi.function

import io.reflekt.plugin.analysis.common.functionNParameterizedType
import io.reflekt.plugin.analysis.ir.toParameterizedTypeVariance
import io.reflekt.plugin.analysis.models.IrFunctionInfo
import io.reflekt.plugin.analysis.models.ParameterizedType
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.isCompanionObject
import org.jetbrains.kotlin.resolve.scopes.receivers.TransientReceiver
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.*

fun KtNamedFunction.getDescriptor(binding: BindingContext): FunctionDescriptor =
    binding.get(BindingContext.FUNCTION, this)!!

fun KtNamedFunction.argumentTypes(binding: BindingContext): List<KotlinType> =
    getDescriptor(binding).valueParameters.map { it.type }

fun KtNamedFunction.argumentTypesWithReceiver(binding: BindingContext): List<KotlinType> =
    listOfNotNull(receiverType(binding)).filter { !it.isObject() }.plus(argumentTypes(binding))

fun KtNamedFunction.returnType(binding: BindingContext): KotlinType? =
    getDescriptor(binding).returnType

fun KtNamedFunction.receiverType(binding: BindingContext): KotlinType? {
    val descriptor = getDescriptor(binding)
    val extensionReceiver = descriptor.extensionReceiverParameter
    val dispatchReceiver = descriptor.dispatchReceiverParameter

    return if (dispatchReceiver != null && dispatchReceiver !is TransientReceiver) {
        dispatchReceiver.type
    } else if (extensionReceiver != null && extensionReceiver !is TransientReceiver) {
        extensionReceiver.type
    } else {
        null
    }
}

fun KtNamedFunction.checkSignature(signature: ParameterizedType, binding: BindingContext): Boolean {
    val argumentTypes = argumentTypesWithReceiver(binding)
    val returnType = returnType(binding) ?: return false
    val functionNParameters = argumentTypes.plus(returnType)
    if (functionNParameters.size != signature.parameters.size) {
        return false
    }
    return (functionNParameters zip signature.parameters).all { (k, p) -> k.equalTo(p, binding) }
}

fun KtNamedFunction.toFunctionInfo(binding: BindingContext): IrFunctionInfo =
    IrFunctionInfo(
        fqName.toString(),
        receiverFqName = receiverType(binding)?.fqName(),
        isObjectReceiver = receiverType(binding)?.isObject() ?: false
    )

fun KtNamedFunction.toSignature(binding: BindingContext): ParameterizedType {
    val r = receiverType(binding)
    val receiverType = listOfNotNull(receiverType(binding)?.let { if (it.isObject()) null else it })
    val argumentTypes = receiverType.plus(argumentTypes(binding)).map { it.toParameterizedType() }
    val returnType = (returnType(binding) ?: error("Found function with no return type")).toParameterizedType()
    return functionNParameterizedType(argumentTypes, returnType)
}

fun KotlinType.fqName() = getJetTypeFqName(false)

fun KotlinType.equalTo(parameterizedType: ParameterizedType, binding: BindingContext): Boolean {
    if (fqName() != parameterizedType.fqName || arguments.size != parameterizedType.parameters.size) {
        return false
    }
    return (arguments.map { it.type } zip parameterizedType.parameters).all { (k, p) -> k.equalTo(p, binding) }
}

fun KotlinType.isObject() = (constructor.declarationDescriptor as? ClassDescriptor)?.kind == ClassKind.OBJECT

fun KotlinType.isCompanionObject() = constructor.declarationDescriptor?.isCompanionObject() ?: false

fun KotlinType.toParameterizedType(): ParameterizedType {
    return ParameterizedType(
        fqName(),
        superTypeFqNames = supertypes().plus(this).map { it.fqName() }.toSet(),
        parameters = arguments.zip(constructor.parameters.map { it.variance }).map { (argument, variance) ->
            argument.type.toParameterizedType()
                .withVariance(argument.projectionKind.toParameterizedTypeVariance())
                .withVariance(variance.toParameterizedTypeVariance())
        },
        nullable = nullability() == TypeNullability.NULLABLE
    )
}
