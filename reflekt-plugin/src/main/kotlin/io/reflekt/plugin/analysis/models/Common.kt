package io.reflekt.plugin.analysis.models


enum class ElementType(val value: String) {
    TypeArgumentList("TYPE_ARGUMENT_LIST"),
    ReferenceExpression("REFERENCE_EXPRESSION"),
    CallExpression("CALL_EXPRESSION"),
    DotQualifiedExpression("DOT_QUALIFIED_EXPRESSION"),
    ValueArgumentList("VALUE_ARGUMENT_LIST")
}
