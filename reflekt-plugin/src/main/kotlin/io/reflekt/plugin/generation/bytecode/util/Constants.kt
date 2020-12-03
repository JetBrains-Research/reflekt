package io.reflekt.plugin.generation.bytecode.util

import org.jetbrains.org.objectweb.asm.Type

enum class AsmType(val internalName: String) {
    OBJECT("java/lang/Object"),
    UNIT("kotlin/Unit"),
    TYPE_CAST_EXCEPTION("kotlin/TypeCastException");

    val type: Type = Type.getObjectType(internalName)
}

enum class MethodName(val text: String) {
    INIT("<init>"),
    INVOKE("invoke")
}

enum class PropertyName(val text: String) {
    INSTANCE("INSTANCE")
}

enum class Keyword(val text: String) {
    THIS("this")
}
