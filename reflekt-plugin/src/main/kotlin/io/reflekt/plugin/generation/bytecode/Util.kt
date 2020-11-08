package io.reflekt.plugin.generation.bytecode

import org.jetbrains.org.objectweb.asm.Label
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

/**
 * Push array of non-primitive type on stack.
 */
fun <T> InstructionAdapter.pushArray(arrayType: Type, items: List<T>, pushItem: InstructionAdapter.(T) -> Unit, checkForNull: Boolean = true) {
    iconst(items.size)
    newarray(arrayType)
    items.forEachIndexed { index, item ->
        dup()
        iconst(index)
        pushItem(item)
        checkCast(arrayType, checkForNull)
        astore(InstructionAdapter.OBJECT_TYPE)
    }
}

fun InstructionAdapter.checkCast(castType: Type, checkForNull: Boolean) {
    if (checkForNull) {
        val nonNullLabel = Label()

        dup()
        ifnonnull(nonNullLabel)
        anew(Type.getObjectType("kotlin/TypeCastException"))
        dup()
        visitLdcInsn("null cannot be cast to non-null type ${castType.className}")
        invokespecial("kotlin/TypeCastException", "<init>", "(Ljava/lang/String;)V", false)
        athrow()
        visitLabel(nonNullLabel)
    }
    checkcast(castType)
}

fun InstructionAdapter.pushObject(type: Type) {
    getstatic(type.internalName, "INSTANCE", type.descriptor)
}

fun InstructionAdapter.pushKClass(type: Type) {
    visitLdcInsn(type)
    invokestatic("kotlin/jvm/internal/Reflection", "getOrCreateKotlinClass", "(Ljava/lang/Class;)Lkotlin/reflect/KClass;", false)
}

fun InstructionAdapter.invokeListOf() {
    invokestatic("kotlin/collections/CollectionsKt", "listOf", "([Ljava/lang/Object;)Ljava/util/List;", false)
}

fun InstructionAdapter.invokeSetOf() {
    invokestatic("kotlin/collections/SetsKt", "setOf", "([Ljava/lang/Object;)Ljava/util/Set;", false)
}
