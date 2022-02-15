package org.jetbrains.reflekt.plugin.ir.type.functions

/**
 * @kotlinType Function1<Any, Any> (kotlin.Function1)
 * @subtypes:
 *   [foo1_MyAlias_Number]
 *   [foo1_Int_Number]
 */
fun foo1_Any_Any(any: Any): Any {

    /**
     * @kotlinType Function1<Any, Any> (kotlin.Function1)
     * @subtypes: no subtypes
     */
    fun foo1_Any_Any_nested(any: Any): Any {

        /**
         * @kotlinType [@kotlin.ExtensionFunctionType] Function1<Any, Any> (kotlin.Function1)
         * @subtypes: no subtypes
         */
        fun Any.foo1_Any_Any_nested_nested(): Any = 0

        return 0
    }

    return 0
}

