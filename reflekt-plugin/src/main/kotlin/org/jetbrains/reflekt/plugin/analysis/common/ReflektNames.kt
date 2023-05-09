package org.jetbrains.reflekt.plugin.analysis.common

import org.jetbrains.kotlin.name.*
import org.jetbrains.reflekt.Reflekt
import org.jetbrains.reflekt.SmartReflekt

/**
 * @property entityType that represents objects/classes/functions
 * @property smartClassName SmartReflekt nested class - [Object/Class/Function]CompileTimeExpression
 */
// Reflekt/SmartReflekt content types
enum class ReflektEntity(
    val entityType: String,
    val smartClassName: String,
) {
    CLASSES("classes", "ClassCompileTimeExpression"),
    FUNCTIONS("functions", "FunctionCompileTimeExpression"),
    OBJECTS("objects", "ObjectCompileTimeExpression"),
    ;

    // Reflekt nested class - Classes/Objects/Functions
    val className = entityType.replaceFirstChar(Char::titlecase)
    val fqName: String = "${Reflekt::class.qualifiedName}.$entityType"
    val classFqName = "${Reflekt::class.qualifiedName}.$className"
    val smartFqName: String = "${SmartReflekt::class.qualifiedName}.$entityType"
    val smartClassFqName = "${SmartReflekt::class.qualifiedName}.$smartClassName"
}

/**
 * @property functionName
 */
enum class ReflektFunction(val functionName: String) {
    WITH_ANNOTATIONS("withAnnotations"),
    WITH_SUPERTYPE("withSuperType"),
    WITH_SUPERTYPES("${WITH_SUPERTYPE.functionName}s"),
    ;
}

/**
 * @property functionName
 */
enum class SmartReflektFunction(val functionName: String) {
    FILTER("filter"),
    ;
}

/**
 * @property className
 */
enum class ReflektNestedClass(val className: String) {
    WITH_ANNOTATIONS(ReflektFunction.WITH_ANNOTATIONS.functionName.replaceFirstChar(Char::titlecase)),
    WITH_SUPERTYPES(ReflektFunction.WITH_SUPERTYPES.functionName.replaceFirstChar(Char::titlecase)),
    ;
}

/**
 * @property functionName
 */
enum class ReflektTerminalFunction(val functionName: String) {
    TO_LIST("toList"),
    TO_SET("toSet"),
    ;
}

/**
 * @property functionName
 */
enum class SmartReflektTerminalFunction(val functionName: String) {
    RESOLVE("resolve"),
    ;
}

object ReflektNames {
    const val PACKAGE_NAME = "org.jetbrains.reflekt"
    const val REFLEKT_CLASS = "ReflektClass"
    const val REFLEKT_CLASS_IMPL = "ReflektClassImpl"
    const val REFLEKT_OBJECT = "ReflektObject"
    const val REFLEKT_VISIBILITY = "ReflektVisibility"
    val PACKAGE_FQ_NAME = FqName(PACKAGE_NAME)
    val REFLEKT_CLASS_NAME = Name.identifier(REFLEKT_CLASS)
    val REFLEKT_CLASS_CLASS_ID = ClassId(PACKAGE_FQ_NAME, REFLEKT_CLASS_NAME)
    val REFLEKT_CLASS_IMPL_NAME = Name.identifier(REFLEKT_CLASS_IMPL)
    val REFLEKT_CLASS_IMPL_CLASS_ID = ClassId(PACKAGE_FQ_NAME, REFLEKT_CLASS_IMPL_NAME)
    val REFLEKT_OBJECT_NAME = Name.identifier(REFLEKT_OBJECT)
    val REFLEKT_OBJECT_CLASS_ID = ClassId(PACKAGE_FQ_NAME, REFLEKT_OBJECT_NAME)
    val REFLEKT_VISIBILITY_NAME = Name.identifier(REFLEKT_VISIBILITY)
    val REFLEKT_VISIBILITY_CLASS_ID = ClassId(PACKAGE_FQ_NAME, REFLEKT_VISIBILITY_NAME)
}

object StorageClassNames {
    const val REFLEKT_CLASSES = "reflektClasses"
    val REFLEKT_CLASSES_NAME = Name.identifier(REFLEKT_CLASSES)

    fun getStorageClass(idx: Int): String = "Storage_$idx"
    fun getStorageClassName(idx: Int): Name = Name.identifier(getStorageClass(idx))
    fun getStorageClassId(idx: Int): ClassId = ClassId(ReflektNames.PACKAGE_FQ_NAME, getStorageClassName(idx))
}
