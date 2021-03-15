package io.reflekt.plugin.analysis.models


enum class ElementType(val value: String) {
    Block("BLOCK"),
    CallExpression("CALL_EXPRESSION"),
    DotQualifiedExpression("DOT_QUALIFIED_EXPRESSION"),
    FunctionLiteral("FUNCTION_LITERAL"),
    FunctionType("FUNCTION_TYPE"),
    LambdaArgument("LAMBDA_ARGUMENT"),
    LambdaExpression("LAMBDA_EXPRESSION"),
    ReferenceExpression("REFERENCE_EXPRESSION"),
    TypeArgumentList("TYPE_ARGUMENT_LIST"),
    TypeProjection("TYPE_PROJECTION"),
    TypeReference("TYPE_REFERENCE"),
    UserType("USER_TYPE"),
    ValueArgumentList("VALUE_ARGUMENT_LIST"),
    ValueParameter("VALUE_PARAMETER"),
    ValueParameterList("VALUE_PARAMETER_LIST"),
}
