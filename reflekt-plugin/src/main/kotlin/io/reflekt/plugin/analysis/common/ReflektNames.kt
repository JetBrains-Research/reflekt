package io.reflekt.plugin.analysis.common

import io.reflekt.Reflekt
import io.reflekt.SmartReflekt

enum class ReflektName(reflektName: String) {
    OBJECTS("objects"),
    CLASSES("classes"),
    FUNCTIONS("functions");

    val className = reflektName.capitalize()
    val fqName: String = "${Reflekt::class.qualifiedName}.$reflektName"
    val smartReflektFqName: String = "${SmartReflekt::class.qualifiedName}.$reflektName"
}

enum class ReflektFunctionName(val functionName: String) {
    WITH_SUBTYPE("withSubType"),
    WITH_SUBTYPES("${WITH_SUBTYPE.functionName}s"),
    WITH_ANNOTATIONS("withAnnotations")
}

enum class SmartReflektFunctionName(val functionName: String) {
    FILTER("filter")
}

enum class ReflektNestedName(val className: String) {
    WITH_SUBTYPES(ReflektFunctionName.WITH_SUBTYPES.functionName.capitalize()),
    WITH_ANNOTATIONS(ReflektFunctionName.WITH_ANNOTATIONS.functionName.capitalize()),
    CLASS_COMPILE_TIME_EXPRESSION("CompileTimeExpression"),
    OBJECT_COMPILE_TIME_EXPRESSION("ObjectCompileTimeExpression"),
    FUNCTION_COMPILE_TIME_EXPRESSION("FunctionCompileTimeExpression")
}

enum class ReflektTerminalFunctionName(val functionName: String) {
    TO_LIST("toList"),
    TO_SET("toSet"),
    RESOLVE("resolve")
}
