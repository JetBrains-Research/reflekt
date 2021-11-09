package io.reflekt.resources.io.reflekt.plugin.analysis.parameterizedtype.functions

class MyClassReceiver : MyInterface {
    /**
     * @kotlinType [@kotlin.ExtensionFunctionType] Function1<MyClassReceiver, String> (kotlin.Function1)
     * @subtypes: no subtypes
     *
     * @return
     */
    fun foo1MyClassReceiverString(): String = "hello"

    /**
     * @kotlinType [@kotlin.ExtensionFunctionType] Function2<MyClassReceiver, Number, Unit> (kotlin.Function2)
     * @subtypes:
     *   [MyClassReceiver.foo2_Any_Any_Unit],
     *   [MyClassReceiver.foo2_MyClassReceiver_NumberExt_Unit]
     *
     * @param n
     */
    fun foo2MyClassReceiverNumberUnit(n: Number) {}

    /**
     * @kotlinType [@kotlin.ExtensionFunctionType] Function2<MyClassReceiver, CharSequence, Unit> (kotlin.Function2)
     * @subtypes:
     *   [MyClassReceiver.foo2_Any_Any_Unit]
     *
     * @param charSequence
     */
    fun foo2MyClassReceiverCharSequenceUnit(charSequence: CharSequence) {}

    /**
     * @kotlinType [@kotlin.ExtensionFunctionType] Function2<MyClassReceiver, Number, Unit> (kotlin.Function2)
     * @subtypes:
     *   [MyClassReceiver.foo2_Any_Any_Unit],
     *   [MyClassReceiver.foo2_MyClassReceiver_Number_Unit]
     */
    fun Number.foo2MyClassReceiverNumberExtUnit() {}

    companion object {
        /**
         * @kotlinType Function0<Number> (kotlin.Function0)
         * @subtypes:
         *   [MyObjectReceiver.foo0_Double],
         *   [foo0_Int],
         *   [foo0_MyAlias],
         *   [foo0_Number]
         *
         * @return
         */
        fun foo0Number(): Number = 0

        /**
         * @kotlinType [@kotlin.ExtensionFunctionType] Function1<Function0<Number>, Unit> (kotlin.Function1)
         * @subtypes [foo1_Any_Unit]
         */
        fun (() -> Number).foo1FunctionalUnit() {}

        /**
         * @kotlinType Function2<Any, Any, Unit> (kotlin.Function2)
         * @subtypes: no subtypes
         *
         * @param a1
         * @param a2
         */
        fun foo2AnyAnyUnit(a1: Any, a2: Any) {}

        /**
         * @kotlinType [@kotlin.ExtensionFunctionType] Function2<MyInterface, Int, Any> (kotlin.Function2)
         * @subtypes:
         *   [MyClassReceiver.foo2_Any_Any_Unit]
         *
         * @param i
         * @return
         */
        fun MyInterface.foo2MyInterfaceIntAny(i: Int): Any = 0
    }
}

object MyObjectReceiver {
    /**
     * @kotlinType Function0<Unit> (kotlin.Function0)
     * @subtypes:
     *   [foo0_Unit],
     *   [fun0_Unit_generic]
     */
    fun foo0Unit() {}

    /**
     * @kotlinType Function0<Double> (kotlin.Function0)
     * @subtypes: no subtypes
     *
     * @return
     */
    fun foo0Double(): Double = 0.0

    /**
     * @kotlinType Function1<MyClassReceiver, Any> (kotlin.Function1)
     * @subtypes:
     *   [MyClassReceiver.foo1_MyClassReceiver_String],
     *   [foo1_Any_Any],
     *   [foo1_Any_Any_nested],
     *   [foo1_Any_Any_nested_nested],
     *   [foo1_Any_Unit],
     *   [foo1_MyClass_CharSequence]
     *
     * @param myClass
     * @return
     */
    fun foo1MyClassReceiverAny(myClass: MyClassReceiver): Any = 0

    /**
     * @kotlinType [@kotlin.ExtensionFunctionType] Function1<Int, Number> (kotlin.Function1)
     * @subtypes:
     *   [foo1_Int_Number],
     *   [foo1_MyAlias_Number]
     *
     * @return
     */
    fun Int.foo1IntNumber(): Number = 0

    /**
     * @kotlinType Function1<Function0<Int>, Unit> (kotlin.Function1)
     * @subtypes:
     *   [MyClassReceiver.foo1_Functional_Unit],
     *   [foo1_Any_Unit]
     *
     * @param f
     */
    fun foo1FunctionalUnit(f: () -> Int) {}
}

/**
 * @kotlinType [@kotlin.ExtensionFunctionType] Function1<String, Unit> (kotlin.Function1)
 * @subtypes:
 *   [foo1_Any_Unit],
 *   [foo1_CharSequence_Unit]
 */
fun String.foo1StringUnit() {}

/**
 * @kotlinType [@kotlin.ExtensionFunctionType] Function1<List<Number?>, Unit> (kotlin.Function1)
 * @subtypes:
 *   [foo1_Any_Unit],
 *   [foo1_ListAny_Unit]
 */
fun List<Number?>.foo1ListNumberUnit() {}

/**
 * @kotlinType [@kotlin.ExtensionFunctionType] Function1<MyClassReceiver, CharSequence> (kotlin.Function1)
 * @subtypes:
 *   [MyClassReceiver.foo1_MyClassReceiver_String]
 *
 * @return
 */
fun MyClassReceiver.foo1MyClassCharSequence(): CharSequence = "hello"

/**
 * @kotlinType [@kotlin.ExtensionFunctionType] Function2<Number, Int, Int> (kotlin.Function2)
 * @subtypes: no subtypes
 *
 * @param int
 * @return
 */
fun Number.foo2NumberIntInt(int: Int): Int = 0

/**
 * @kotlinType [@kotlin.ExtensionFunctionType] Function2<MyInterface, Array<out Int>, Unit> (kotlin.Function2)
 * @subtypes:
 *   [MyClassReceiver.foo2_Any_Any_Unit],
 *   [foo2_MyInterface_ArrayOutNumber_Unit]
 *
 * @param array
 */
fun MyInterface.foo2MyInterfaceArrayOutIntUnit(array: Array<out Int>) {}

/**
 * @kotlinType [@kotlin.ExtensionFunctionType] Function2<MyInterface, Array<out Number>, Unit> (kotlin.Function2)
 * @subtypes:
 *   [MyClassReceiver.foo2_Any_Any_Unit]
 *
 * @param array
 */
fun MyInterface.foo2MyInterfaceArrayOutNumberUnit(array: Array<out Number>) {}
