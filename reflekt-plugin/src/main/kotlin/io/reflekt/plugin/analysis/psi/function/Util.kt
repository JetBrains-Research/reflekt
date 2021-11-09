package io.reflekt.plugin.analysis.psi.function

import io.reflekt.plugin.analysis.models.IrFunctionInfo
import org.jetbrains.kotlin.builtins.*
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.isCompanionObject
import org.jetbrains.kotlin.resolve.scopes.receivers.TransientReceiver
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeUtils.equalTypes
import org.jetbrains.kotlin.types.expressions.createFunctionType

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

// Todo: do we actually need equal types or being subtype is enough?
fun KtNamedFunction.checkSignature(signature: KotlinType, binding: BindingContext) = this.toParameterizedType(binding)?.let { equalTypes(it, signature) } ?: false

fun KtNamedFunction.toFunctionInfo(binding: BindingContext): IrFunctionInfo =
        IrFunctionInfo(
            fqName.toString(),
            receiverFqName = receiverType(binding)?.shortFqName(),
            isObjectReceiver = receiverType(binding)?.isObject() ?: false,
        )

fun KtNamedFunction.toParameterizedType(binding: BindingContext): KotlinType? = getDescriptor(binding).toParameterizedType()

fun FunctionDescriptor.toParameterizedType() = (this as? SimpleFunctionDescriptor)?.createFunctionTypeWithDispatchReceiver(DefaultBuiltIns.Instance)

/**
 * We need to create FunctionType from function descriptor, but unlike [SimpleFunctionDescriptor.createFunctionType] we want to take into account
 * its dispatch receiver, since the existing implementation only cares about extension receiver.
 *
 *  extension receiver
 *        v
 * fun String.fooString() { ... }               --->    Function1<String, Unit>
 *
 *
 *** Note: the function above has the same KotlinType as the one below ***
 * TODO: do we want to distinguish them?
 *
 * fun foo(s: String): Unit { ... }             --->    Function1<String, Unit>
 *
 *
 *    dispatch receiver
 *          v
 * class MyClass {
 *     fun fooClass() { ... }                   --->    Function1<MyClass, Unit>
 *
 *     fun String.fooStringClass() { ... }      --->    Function2<MyClass, String, Unit>
 *           ^
 *     extension receiver
 * }
 *
 * In case of having both dispatch and extension receiver, we give priority to the dispatch receiver, since such functions
 * can only be called in the scope of dispatch receiver (i.e. inside MyClass or scope functions)
 *
 * However, if function's dispatch receiver is an object (or a companion object), we ignore it,
 * since we can call the function without it:
 *
 *    dispatch receiver (ignore)
 *            v
 * object MyObject {
 *     fun fooObject() { ... }                  --->    Function0<Unit>
 *
 *
 *     fun String.fooStringObject() { ... }     --->    Function1<String, Unit>
 *          ^
 *     extension receiver
 *
 * @param builtIns
 * @param suspendFunction
 * @param shouldUseVarargType
 * @return
 */
fun SimpleFunctionDescriptor.createFunctionTypeWithDispatchReceiver(
    builtIns: KotlinBuiltIns,
    suspendFunction: Boolean = false,
    shouldUseVarargType: Boolean = false,
): KotlinType? {
    // If function is inside an object (or companion object), we dont't want to consider its dispatch receiver
    val dispatchReceiver = if (this.dispatchReceiverParameter?.containingDeclaration.isObject()) {
        null
    } else {
        this.dispatchReceiverParameter
    }

    val extensionReceiver = this.extensionReceiverParameter

    // If dispatch receiver is null, we take extension receiver as receiver (it's okay if it's also null, createFunctionType takes care of it)
    val receiver = dispatchReceiver ?: extensionReceiver
    val parameters = if (shouldUseVarargType) {
        valueParameters.map { it.varargElementType ?: it.type }.toMutableList()
    } else {
        valueParameters.map { it.type }.toMutableList()
    }
    // If function has both receivers, we need to add its extension receiver to its parameters
    if (dispatchReceiver != null && extensionReceiver != null) {
        parameters.add(0, extensionReceiver.type)
    }

    return createFunctionType(
        builtIns,
        annotations,
        receiver?.type,
        parameters,
        null,
        returnType ?: return null,
        suspendFunction = suspendFunction,
    )
}

fun DeclarationDescriptor?.isObject() = (this as? ClassDescriptor)?.kind == ClassKind.OBJECT

fun KotlinType.shortFqName() = getJetTypeFqName(false)

fun KotlinType.isObject() = constructor.declarationDescriptor.isObject()

fun KotlinType.isCompanionObject() = constructor.declarationDescriptor?.isCompanionObject() ?: false
