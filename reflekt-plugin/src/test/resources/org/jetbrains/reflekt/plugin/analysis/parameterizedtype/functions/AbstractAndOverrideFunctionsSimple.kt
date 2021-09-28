package org.jetbrains.reflekt.plugin.analysis.parameterizedtype.functions

interface MySimpleInterface {

    /**
     * @kotlinType [@kotlin.ExtensionFunctionType] Function1<MySimpleInterface, Any> (kotlin.Function1)
     * @subtypes:
     *   [foo1_Any_Any],
     *   [foo1_Any_Any_nested],
     *   [foo1_Any_Any_nested_nested],
     *   [foo1_Any_Unit]
     */
    fun foo1_MySimpleInterface_Any(): Any

}


class MySimpleClass : MySimpleInterface {

    /**
     * @kotlinType [@kotlin.ExtensionFunctionType] Function1<MySimpleClass, Any> (kotlin.Function1)
     * @subtypes:
     *   [MySimpleInterface.foo1_MySimpleInterface_Any],
     *   [foo1_Any_Any],
     *   [foo1_Any_Any_nested],
     *   [foo1_Any_Any_nested_nested],
     *   [foo1_Any_Unit]
     */
    override fun foo1_MySimpleInterface_Any(): Any = 0
}
