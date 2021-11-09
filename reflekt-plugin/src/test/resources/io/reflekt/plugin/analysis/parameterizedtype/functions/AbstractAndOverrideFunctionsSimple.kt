package io.reflekt.resources.io.reflekt.plugin.analysis.parameterizedtype.functions

interface MySimpleInterface {
    /**
     * @kotlinType [@kotlin.ExtensionFunctionType] Function1<MySimpleInterface, Any> (kotlin.Function1)
     * @subtypes:
     *   [foo1_Any_Any],
     *   [foo1_Any_Any_nested],
     *   [foo1_Any_Any_nested_nested],
     *   [foo1_Any_Unit]
     *
     * @return
     */
    fun foo1MySimpleInterfaceAny(): Any
}

object MySimpleClass : MySimpleInterface {
    /**
     * @kotlinType [@kotlin.ExtensionFunctionType] Function1<MySimpleClass, Any> (kotlin.Function1)
     * @subtypes:
     *   [MySimpleInterface.foo1_MySimpleInterface_Any],
     *   [foo1_Any_Any],
     *   [foo1_Any_Any_nested],
     *   [foo1_Any_Any_nested_nested],
     *   [foo1_Any_Unit]
     */
    override fun foo1MySimpleInterfaceAny(): Any = 0
}
