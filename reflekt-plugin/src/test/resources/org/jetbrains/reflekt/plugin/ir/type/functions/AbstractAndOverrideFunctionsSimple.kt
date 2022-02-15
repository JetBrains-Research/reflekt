package org.jetbrains.reflekt.plugin.ir.type.functions

interface MySimpleInterface {

    /**
     * @kotlinType [@kotlin.ExtensionFunctionType] Function1<MySimpleInterface, Any> (kotlin.Function1)
     * @subtypes:
     *   [MySimpleClass.foo1_MySimpleInterface_Any]
     */
    fun foo1_MySimpleInterface_Any(): Any

}


class MySimpleClass : MySimpleInterface {

    /**
     * @kotlinType [@kotlin.ExtensionFunctionType] Function1<MySimpleClass, Any> (kotlin.Function1)
     * @subtypes: no subtypes
     */
    override fun foo1_MySimpleInterface_Any(): Any = 0
}
