package io.reflekt.resources.io.reflekt.plugin.analysis.parameterizedtype.functions

/**
 * @kotlinType Function1<Any, Any> (kotlin.Function1)
 * @subtypes:
 *   [foo1_Any_Any_nested],
 *   [foo1_Any_Any_nested_nested],
 *   [foo1_Any_Unit]
 *
 * @param any
 * @return
 */
fun foo1AnyAny(any: Any): Any {
    /**
     * @kotlinType Function1<Any, Any> (kotlin.Function1)
     * @subtypes:
     *   [foo1_Any_Any],
     *   [foo1_Any_Any_nested_nested],
     *   [foo1_Any_Unit]
     *
     * @param any
     * @return
     */
    fun foo1AnyAnyNested(any: Any): Any {
        /**
         * @kotlinType [@kotlin.ExtensionFunctionType] Function1<Any, Any> (kotlin.Function1)
         * @subtypes:
         *   [foo1_Any_Any],
         *   [foo1_Any_Any_nested],
         *   [foo1_Any_Unit]
         *
         * @return
         */
        fun Any.foo1AnyAnyNestedNested(): Any = 0

        return 0
    }

    return 0
}
