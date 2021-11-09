package io.reflekt.resources.io.reflekt.plugin.analysis.parameterizedtype.functions

interface MyInterfaceWithFunctions {
    /**
     * @kotlinType [@kotlin.ExtensionFunctionType] Function2<MyInterfaceWithFunctions, Any, Any> (kotlin.Function2)
     * @subtypes:
     *   [MyInterfaceWithFunctions.foo2_MyInterfaceWithFunctions_Any_Any_abstract],
     *   [MyClassReceiver.foo2_Any_Any_Unit]
     *
     * @param any
     * @return
     */
    fun foo2MyInterfaceWithFunctionsAnyAny(any: Any): Any = "hello"

    /**
     * @kotlinType [@kotlin.ExtensionFunctionType] Function2<MyInterfaceWithFunctions, Any, Any> (kotlin.Function2)
     * @subtypes:
     *   [MyInterfaceWithFunctions.foo2_MyInterfaceWithFunctions_Any_Any],
     *   [MyClassReceiver.foo2_Any_Any_Unit]
     *
     * @param any
     * @return
     */
    fun foo2MyInterfaceWithFunctionsAnyAnyAbstract(any: Any): Any

    /**
     * @kotlinType [@kotlin.ExtensionFunctionType] Function1<MyInterfaceWithFunctions, Unit> (kotlin.Function1)
     * @subtypes:
     *   [foo1_Any_Unit]
     */
    fun foo1MyInterfaceWithFunctionsUnit() {}
}

abstract object MyAbstractClass : MyInterfaceWithFunctions {
    /**
     * @kotlinType [@kotlin.ExtensionFunctionType] Function1<MyAbstractClass, Unit> (kotlin.Function1)
     * @subtypes:
     *   [MyInterfaceWithFunctions.foo1_MyInterfaceWithFunctions_Unit],
     *   [foo1_Any_Unit]
     */
    abstract fun foo1MyAbstractClassUnit()
}

class MyClassWithFunctions : MyAbstractClass() {
    /**
     * @kotlinType [@kotlin.ExtensionFunctionType] Function1<MyClassWithFunctions, Unit> (kotlin.Function1)
     * @subtypes:
     *   [MyInterfaceWithFunctions.foo1_MyInterfaceWithFunctions_Unit],
     *   [MyAbstractClass.foo1_MyAbstractClass_Unit]
     *   [foo1_Any_Unit]
     */
    override fun foo1MyAbstractClassUnit() { }

    /**
     * @kotlinType [@kotlin.ExtensionFunctionType] Function2<MyClassWithFunctions, Any, Any> (kotlin.Function2)
     * @subtypes:
     *  [MyInterfaceWithFunctions.foo2_MyInterfaceWithFunctions_Any_Any],
     *  [MyInterfaceWithFunctions.foo2_MyInterfaceWithFunctions_Any_Any_abstract],
     *  [MyClassWithFunctions.foo2_MyInterfaceWithFunctions_Any_Any_abstract],
     *  [MyClassReceiver.foo2_Any_Any_Unit]
     */
    override fun foo2MyInterfaceWithFunctionsAnyAny(any: Any): Any = 0

    /**
     * @kotlinType [@kotlin.ExtensionFunctionType] Function2<MyClassWithFunctions, Any, Any> (kotlin.Function2)
     * @subtypes:
     *   [MyClassReceiver.foo2_Any_Any_Unit],
     *   [MyInterfaceWithFunctions.foo2_MyInterfaceWithFunctions_Any_Any],
     *   [MyInterfaceWithFunctions.foo2_MyInterfaceWithFunctions_Any_Any_abstract],
     *   [MyClassWithFunctions.foo2_MyInterfaceWithFunctions_Any_Any]
     */
    override fun foo2MyInterfaceWithFunctionsAnyAnyAbstract(any: Any): Any = 0
}
