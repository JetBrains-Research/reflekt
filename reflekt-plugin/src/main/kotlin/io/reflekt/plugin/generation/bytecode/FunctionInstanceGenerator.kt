package io.reflekt.plugin.generation.bytecode

import io.reflekt.plugin.generation.bytecode.util.*
import io.reflekt.plugin.utils.Util.log
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.codegen.ClassBuilder
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.kotlin.codegen.asmType
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.codegen.signature.BothSignatureWriter
import org.jetbrains.kotlin.codegen.state.GenerationState
import org.jetbrains.kotlin.codegen.state.KotlinTypeMapper
import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.load.kotlin.TypeMappingMode
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin
import org.jetbrains.kotlin.resolve.jvm.jvmSignature.JvmMethodParameterKind
import org.jetbrains.kotlin.resolve.scopes.receivers.TransientReceiver
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter
import org.jetbrains.org.objectweb.asm.commons.Method

class FunctionInstanceGenerator(
    private val classNamePrefix: String,
    private val messageCollector: MessageCollector?
) {
    private var nextIndex = 1

    fun generate(
        function: KtNamedFunction,
        c: ExpressionCodegenExtension.Context
    ): Type = GeneratorImpl(function, c).generate()

    private inner class GeneratorImpl(
        function: KtNamedFunction,
        c: ExpressionCodegenExtension.Context
    ) {
        val binding: BindingContext = c.codegen.bindingContext
        val typeMapper: KotlinTypeMapper = c.typeMapper
        val state: GenerationState = c.codegen.state

        val functionDescriptor: FunctionDescriptor = binding.get(BindingContext.FUNCTION, function)!!

        val receiverType: KotlinType? = let {
            val extensionReceiver = functionDescriptor.extensionReceiverParameter
            val dispatchReceiver = functionDescriptor.dispatchReceiverParameter

            if (dispatchReceiver != null && dispatchReceiver !is TransientReceiver) {
                dispatchReceiver.type
            } else if (extensionReceiver != null && extensionReceiver !is TransientReceiver) {
                extensionReceiver.type
            } else {
                null
            }
        }

        val isObjectReceiver = (receiverType?.constructor?.declarationDescriptor as? ClassDescriptor)?.kind == ClassKind.OBJECT

        val argumentTypes: List<KotlinType> = let {
            listOfNotNull(if (!isObjectReceiver) receiverType else null).plus(
                functionDescriptor.valueParameters.map { it.type }
            )
        }
        val returnType: KotlinType = functionDescriptor.returnType!!

        val classAsmType: Type = Type.getObjectType("$classNamePrefix$${nextIndex++}")

        val receiverAsmType: Type? = receiverType?.asmType(typeMapper)
        val argumentAsmTypes: List<Type> = argumentTypes.map { it.asmType(typeMapper) }
        val resultAsmType: Type = returnType.asmType(typeMapper)
        val returnAsmType: Type = if (resultAsmType == AsmType.UNIT.type) Type.VOID_TYPE else resultAsmType

        fun generate(): Type {
            val functionInterface = "kotlin/jvm/functions/Function${argumentTypes.size}"
            val classSignature = generateClassSignature(Type.getObjectType(functionInterface))

            val classBuilder = state.factory.newVisitor(JvmDeclarationOrigin.NO_ORIGIN, classAsmType, listOf<PsiFile>())
            classBuilder.defineClass(
                null,
                state.classFileVersion,
                Opcodes.ACC_FINAL or Opcodes.ACC_SUPER or Opcodes.ACC_PUBLIC,
                classAsmType.internalName,
                generateClassSignature(Type.getObjectType(functionInterface)),
                AsmType.OBJECT.internalName,
                arrayOf(functionInterface)
            )

            messageCollector?.log("GENERATED CLASS SIGNATURE: ${classSignature}")

            generateInit(classBuilder)
            generateInvoke(classBuilder)
            classBuilder.done()

            return classAsmType
        }

        fun generateClassSignature(functionInterfaceType: Type): String {
            val signatureWriter = BothSignatureWriter(BothSignatureWriter.Mode.CLASS)
            signatureWriter.writeSuperclass {
                signatureWriter.writeAsmType(AsmType.OBJECT.type)
            }
            signatureWriter.writeInterface {
                signatureWriter.writeClass(functionInterfaceType) {
                    argumentTypes.plus(returnType).forEach {
                        signatureWriter.writeTypeArgument(Variance.INVARIANT) {
                            typeMapper.mapType(it, signatureWriter, TypeMappingMode.GENERIC_ARGUMENT)
                        }
                    }
                }
            }
            return signatureWriter.toString()
        }

        fun generateInvokeSignature(): String {
            val signatureWriter = BothSignatureWriter(BothSignatureWriter.Mode.METHOD)
            argumentTypes.forEach {
                signatureWriter.writeParameterType(JvmMethodParameterKind.VALUE) {
                    typeMapper.mapType(it, signatureWriter, TypeMappingMode.getOptimalModeForValueParameter(it))
                }
            }
            signatureWriter.writeReturnType {
                if (returnAsmType == Type.VOID_TYPE) {
                    signatureWriter.writeAsmType(returnAsmType)
                } else {
                    typeMapper.mapType(returnType, signatureWriter, TypeMappingMode.getOptimalModeForReturnType(returnType, false))
                }
            }
            return signatureWriter.toString()
        }

        fun generateInit(classBuilder: ClassBuilder) {
            val methodVisitor = InstructionAdapter(classBuilder.newMethod(
                JvmDeclarationOrigin.NO_ORIGIN,
                Opcodes.ACC_PUBLIC,
                MethodName.INIT.text,
                "()V",
                null,
                null
            ))
            methodVisitor.visitCode {
                val begin = methodVisitor.newLabel()

                methodVisitor.load(0, classAsmType)
                methodVisitor.invokespecial(AsmType.OBJECT.internalName, MethodName.INIT.text, "()V", false)
                methodVisitor.areturn(Type.VOID_TYPE)

                val end = methodVisitor.newLabel()
                methodVisitor.visitLocalVariable(Keyword.THIS.text, classAsmType.descriptor, null, begin, end, 0)
            }
        }

        fun generateInvoke(classBuilder: ClassBuilder) {
            val method = Method(MethodName.INVOKE.text, returnAsmType, argumentAsmTypes.toTypedArray())
            val signature = generateInvokeSignature()

            val methodVisitor = InstructionAdapter(classBuilder.newMethod(
                JvmDeclarationOrigin.NO_ORIGIN,
                Opcodes.ACC_PUBLIC,
                method.name,
                method.descriptor,
                signature,
                null
            ))

            methodVisitor.visitCode {
                val beginLabel = methodVisitor.newLabel()
                methodVisitor.invokeReferencedFunction()
                methodVisitor.areturn(returnAsmType)
                val endLabel = methodVisitor.newLabel()

                methodVisitor.visitLocalVariable(Keyword.THIS.text, classAsmType.descriptor, null, beginLabel, endLabel, 0)
                argumentAsmTypes.forEachIndexed { i, t ->
                    methodVisitor.visitLocalVariable("p${i + 1}", t.descriptor, null, beginLabel, endLabel, i + 1)
                }
            }

            generateInvokeBridge(classBuilder, method.descriptor)

            messageCollector?.log("GENERATED INVOKE SIGNATURE: $signature")
        }

        private fun generateInvokeBridge(classBuilder: ClassBuilder, invokeDescriptor: String) {
            val method = Method(MethodName.INVOKE.text, AsmType.OBJECT.type, Array(argumentAsmTypes.size) { AsmType.OBJECT.type })

            val methodVisitor = InstructionAdapter(classBuilder.newMethod(
                JvmDeclarationOrigin.NO_ORIGIN,
                Opcodes.ACC_PUBLIC or Opcodes.ACC_SYNTHETIC or Opcodes.ACC_BRIDGE,
                method.name,
                method.descriptor,
                null,
                null
            ))

            methodVisitor.visitCode {
                methodVisitor.newLabel()

                methodVisitor.load(0, classAsmType)
                var slot = 1
                argumentTypes.forEach { t ->
                    val value = StackValue.local(slot, AsmType.OBJECT.type, t)
                    val targetType = typeMapper.mapType(t)
                    value.put(targetType, methodVisitor)
                    slot += targetType.size
                }
                methodVisitor.invokevirtual(classAsmType.internalName, MethodName.INVOKE.text, invokeDescriptor, false)

                if (returnAsmType == Type.VOID_TYPE) {
                    methodVisitor.pushObject(AsmType.UNIT.type)
                }

                methodVisitor.areturn(resultAsmType)

                methodVisitor.newLabel()
            }
        }

        fun InstructionAdapter.invokeReferencedFunction() {
            val method = typeMapper.mapToCallableMethod(functionDescriptor, superCall = false)
            if (isObjectReceiver && receiverAsmType != null) {
                pushObject(receiverAsmType)
            }
            argumentAsmTypes.forEachIndexed { index, asmType ->
                load(index + 1, asmType)
            }
            method.genInvokeInstruction(this)
        }
    }
}
