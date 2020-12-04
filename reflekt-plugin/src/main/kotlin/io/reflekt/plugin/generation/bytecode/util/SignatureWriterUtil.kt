package io.reflekt.plugin.generation.bytecode.util

import org.jetbrains.kotlin.codegen.signature.JvmSignatureWriter
import org.jetbrains.kotlin.resolve.jvm.jvmSignature.JvmMethodParameterKind
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.org.objectweb.asm.Type

fun JvmSignatureWriter.writeClass(asmType: Type, action: () -> Unit) {
    writeClassBegin(asmType)
    action()
    writeClassEnd()
}

fun JvmSignatureWriter.writeSuperclass(action: () -> Unit) {
    writeSuperclass()
    action()
    writeSuperclassEnd()
}

fun JvmSignatureWriter.writeInterface(action: () -> Unit) {
    writeInterface()
    action()
    writeInterfaceEnd()
}

fun JvmSignatureWriter.writeTypeArgument(variance: Variance, action: () -> Unit) {
    writeTypeArgument(variance)
    action()
    writeTypeArgumentEnd()
}

fun JvmSignatureWriter.writeParameterType(kind: JvmMethodParameterKind, action: () -> Unit) {
    writeParameterType(kind)
    action()
    writeParameterTypeEnd()
}

fun JvmSignatureWriter.writeReturnType(action: () -> Unit) {
    writeReturnType()
    action()
    writeReturnTypeEnd()
}
