package org.jetbrains.reflekt.plugin.analysis.parameterizedtype.functions

/**
 * @kotlinType Function1<Any, Any> (kotlin.Function1)
 * @subtypes:
 *   [foo1_Any_Any_nested],
 *   [foo1_Any_Any_nested_nested],
 *   [foo1_Any_Unit]
 */
fun foo1_Any_Any(any: Any): Any {

    /**
     * @kotlinType Function1<Any, Any> (kotlin.Function1)
     * @subtypes:
     *   [foo1_Any_Any],
     *   [foo1_Any_Any_nested_nested],
     *   [foo1_Any_Unit]
     */
    fun foo1_Any_Any_nested(any: Any): Any {

        /**
         * @kotlinType [@kotlin.ExtensionFunctionType] Function1<Any, Any> (kotlin.Function1)
         * @subtypes:
         *   [foo1_Any_Any],
         *   [foo1_Any_Any_nested],
         *   [foo1_Any_Unit]
         */
        fun Any.foo1_Any_Any_nested_nested(): Any = 0

        return 0
    }

    return 0
}

