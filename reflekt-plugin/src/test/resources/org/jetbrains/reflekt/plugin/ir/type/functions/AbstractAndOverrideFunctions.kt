package org.jetbrains.reflekt.plugin.ir.type.functions

interface MyInterfaceWithFunctions {
    /**
     * @kotlinType [@kotlin.ExtensionFunctionType] Function2<MyInterfaceWithFunctions, Any, Any> (kotlin.Function2)
     * @subtypes:
     *   [MyInterfaceWithFunctions.foo2_MyInterfaceWithFunctions_Any_Any_abstract]
     *   [MyClassWithFunctions.foo2_MyInterfaceWithFunctions_Any_Any]
     *   [MyClassWithFunctions.foo2_MyInterfaceWithFunctions_Any_Any_abstract]
     */
    fun foo2_MyInterfaceWithFunctions_Any_Any(any: Any): Any = "hello"

    /**
     * @kotlinType [@kotlin.ExtensionFunctionType] Function2<MyInterfaceWithFunctions, Any, Any> (kotlin.Function2)
     * @subtypes:
     *   [MyInterfaceWithFunctions.foo2_MyInterfaceWithFunctions_Any_Any]
     *   [MyClassWithFunctions.foo2_MyInterfaceWithFunctions_Any_Any]
     *   [MyClassWithFunctions.foo2_MyInterfaceWithFunctions_Any_Any_abstract]
     */
    fun foo2_MyInterfaceWithFunctions_Any_Any_abstract(any: Any): Any

    /**
     * @kotlinType [@kotlin.ExtensionFunctionType] Function1<MyInterfaceWithFunctions, Unit> (kotlin.Function1)
     * @subtypes:
     *   [MyAbstractClass.foo1_MyAbstractClass_Unit]
     *   [MyClassWithFunctions.foo1_MyAbstractClass_Unit]
     */
    fun foo1_MyInterfaceWithFunctions_Unit() {}
}

abstract class MyAbstractClass : MyInterfaceWithFunctions {
    /**
     * @kotlinType [@kotlin.ExtensionFunctionType] Function1<MyAbstractClass, Unit> (kotlin.Function1)
     * @subtypes:
     *   [MyClassWithFunctions.foo1_MyAbstractClass_Unit]
     */
    abstract fun foo1_MyAbstractClass_Unit()
}

class MyClassWithFunctions : MyAbstractClass() {

    /**
     * @kotlinType [@kotlin.ExtensionFunctionType] Function1<MyClassWithFunctions, Unit> (kotlin.Function1)
     * @subtypes: no subtypes
     */
    override fun foo1_MyAbstractClass_Unit() { }

    /**
     * @kotlinType [@kotlin.ExtensionFunctionType] Function2<MyClassWithFunctions, Any, Any> (kotlin.Function2)
     * @subtypes:
     *  [MyClassWithFunctions.foo2_MyInterfaceWithFunctions_Any_Any_abstract]
     */
    override fun foo2_MyInterfaceWithFunctions_Any_Any(any: Any): Any = 0

    /**
     * @kotlinType [@kotlin.ExtensionFunctionType] Function2<MyClassWithFunctions, Any, Any> (kotlin.Function2)
     * @subtypes:
     *   [MyClassWithFunctions.foo2_MyInterfaceWithFunctions_Any_Any]
     */
    override fun foo2_MyInterfaceWithFunctions_Any_Any_abstract(any: Any): Any = 0
}
