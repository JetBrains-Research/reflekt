package io.reflekt.plugin.analysis.models


enum class ElementType(val value: String) {
    TypeArgumentList("TYPE_ARGUMENT_LIST"),
    ReferenceExpression("REFERENCE_EXPRESSION"),
    CallExpression("CALL_EXPRESSION"),
    DotQualifiedExpression("DOT_QUALIFIED_EXPRESSION"),
    ValueArgumentList("VALUE_ARGUMENT_LIST"),
    ValueParameterList("VALUE_PARAMETER_LIST"),
    LambdaArgument("LAMBDA_ARGUMENT"),
    LambdaExpression("LAMBDA_EXPRESSION"),
    FunctionLiteral("FUNCTION_LITERAL"),
    Block("BLOCK")
}
