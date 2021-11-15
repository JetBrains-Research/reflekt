package io.reflekt.plugin.analysis.models

/**
 * @property value
 */
enum class ElementType(val value: String) {
    BLOCK("BLOCK"),
    CALL_EXPRESSION("CALL_EXPRESSION"),
    DOT_QUALIFIED_EXPRESSION("DOT_QUALIFIED_EXPRESSION"),
    FILE("kotlin.FILE"),
    FUNCTION_LITERAL("FUNCTION_LITERAL"),
    FUNCTION_TYPE("FUNCTION_TYPE"),
    LAMBDA_ARGUMENT("LAMBDA_ARGUMENT"),
    LAMBDA_EXPRESSION("LAMBDA_EXPRESSION"),
    NULLABLE_TYPE("NULLABLE_TYPE"),
    REFERENCE_EXPRESSION("REFERENCE_EXPRESSION"),
    TYPE_ARGUMENT_LIST("TYPE_ARGUMENT_LIST"),
    TYPE_PROJECTION("TYPE_PROJECTION"),
    TYPE_REFERENCE("TYPE_REFERENCE"),
    USER_TYPE("USER_TYPE"),
    VALUE_ARGUMENT_LIST("VALUE_ARGUMENT_LIST"),
    VALUE_PARAMETER("VALUE_PARAMETER"),
    VALUE_PARAMETER_LIST("VALUE_PARAMETER_LIST"),
    ;
}

/**
 * @property uses
 * @property instances
 */
data class ReflektContext(
    var uses: IrReflektUses? = null,
    var instances: IrReflektInstances? = null,
)
