package io.reflekt.plugin.analysis.common

import io.reflekt.Reflekt
import io.reflekt.SmartReflekt

// Reflekt/SmartReflekt content types
enum class ReflektEntity(
    val entityType: String,     // objects/classes/functions
    val smartClassName: String  // SmartReflekt nested class - [Object/Class/Function]CompileTimeExpression
) {
    OBJECTS("objects", "ObjectCompileTimeExpression"),
    CLASSES("classes", "ClassCompileTimeExpression"),
    FUNCTIONS("functions", "FunctionCompileTimeExpression");

    // Reflekt nested class - Classes/Objects/Functions
    val className = entityType.capitalize()

    val fqName: String = "${Reflekt::class.qualifiedName}.$entityType"
    val classFqName = "${Reflekt::class.qualifiedName}.$className"

    val smartFqName: String = "${SmartReflekt::class.qualifiedName}.$entityType"
    val smartClassFqName = "${SmartReflekt::class.qualifiedName}.$smartClassName"
}

enum class ReflektFunction(val functionName: String) {
    WITH_SUBTYPE("withSubType"),
    WITH_SUBTYPES("${WITH_SUBTYPE.functionName}s"),
    WITH_ANNOTATIONS("withAnnotations"),
}

enum class SmartReflektFunction(val functionName: String) {
    FILTER("filter")
}

enum class ReflektNestedClass(val className: String) {
    WITH_SUBTYPES(ReflektFunction.WITH_SUBTYPES.functionName.capitalize()),
    WITH_ANNOTATIONS(ReflektFunction.WITH_ANNOTATIONS.functionName.capitalize()),
}

enum class ReflektTerminalFunction(val functionName: String) {
    TO_LIST("toList"),
    TO_SET("toSet"),
}

enum class SmartReflektTerminalFunction(val functionName: String) {
    RESOLVE("resolve")
}
