package org.jetbrains.reflekt.plugin.analysis.common

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
 * @property propertyName
 */
enum class ReflektClassRegistry(val propertyName: String) {
    REFLEKT_CLASSES("reflektClasses"),
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
