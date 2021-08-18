package io.reflekt.plugin.analysis.parameterizedtype.functions


class MyClassReceiver : MyInterface {

    /**
     * @kotlinType [@kotlin.ExtensionFunctionType] Function1<MyClassReceiver, String> (kotlin.Function1)
     * @subtypes: no subtypes
     */
    fun foo1_MyClassReceiver_String(): String = "hello"

    /**
     * @kotlinType [@kotlin.ExtensionFunctionType] Function2<MyClassReceiver, Number, Unit> (kotlin.Function2)
     * @subtypes:
     *   [MyClassReceiver.foo2_Any_Any_Unit],
     *   [MyClassReceiver.foo2_MyClassReceiver_NumberExt_Unit]
     */
    fun foo2_MyClassReceiver_Number_Unit(n: Number) {}

    /**
     * @kotlinType [@kotlin.ExtensionFunctionType] Function2<MyClassReceiver, CharSequence, Unit> (kotlin.Function2)
     * @subtypes:
     *   [MyClassReceiver.foo2_Any_Any_Unit]
     */
    fun foo2_MyClassReceiver_CharSequence_Unit(charSequence: CharSequence) {}

    /**
     * @kotlinType [@kotlin.ExtensionFunctionType] Function2<MyClassReceiver, Number, Unit> (kotlin.Function2)
     * @subtypes:
     *   [MyClassReceiver.foo2_Any_Any_Unit],
     *   [MyClassReceiver.foo2_MyClassReceiver_Number_Unit]
     */
    fun Number.foo2_MyClassReceiver_NumberExt_Unit() {}


    companion object {

        /**
         * @kotlinType Function0<Number> (kotlin.Function0)
         * @subtypes:
         *   [MyObjectReceiver.foo0_Double],
         *   [foo0_Int],
         *   [foo0_MyAlias],
         *   [foo0_Number]
         */
        fun foo0_Number(): Number = 0

        /**
         * @kotlinType [@kotlin.ExtensionFunctionType] Function1<Function0<Number>, Unit> (kotlin.Function1)
         * @subtypes [foo1_Any_Unit]
         */
        fun (() -> Number).foo1_Functional_Unit() {}

        /**
         * @kotlinType Function2<Any, Any, Unit> (kotlin.Function2)
         * @subtypes: no subtypes
         */
        fun foo2_Any_Any_Unit(a1: Any, a2: Any) {}

        /**
         * @kotlinType [@kotlin.ExtensionFunctionType] Function2<MyInterface, Int, Any> (kotlin.Function2)
         * @subtypes:
         *   [MyClassReceiver.foo2_Any_Any_Unit]
         */
        fun MyInterface.foo2_MyInterface_Int_Any(i: Int): Any = 0
    }
}


object MyObjectReceiver {

    /**
     * @kotlinType Function0<Unit> (kotlin.Function0)
     * @subtypes:
     *   [foo0_Unit],
     *   [fun0_Unit_generic]
     */
    fun foo0_Unit() {}

    /**
     * @kotlinType Function0<Double> (kotlin.Function0)
     * @subtypes: no subtypes
     */
    fun foo0_Double(): Double = 0.0

    /**
     * @kotlinType Function1<MyClassReceiver, Any> (kotlin.Function1)
     * @subtypes:
     *   [MyClassReceiver.foo1_MyClassReceiver_String],
     *   [foo1_Any_Any],
     *   [foo1_Any_Any_nested],
     *   [foo1_Any_Any_nested_nested],
     *   [foo1_Any_Unit],
     *   [foo1_MyClass_CharSequence]
     */
    fun foo1_MyClassReceiver_Any(myClass: MyClassReceiver): Any = 0

    /**
     * @kotlinType [@kotlin.ExtensionFunctionType] Function1<Int, Number> (kotlin.Function1)
     * @subtypes:
     *   [foo1_Int_Number],
     *   [foo1_MyAlias_Number]
     */
    fun Int.foo1_Int_Number(): Number = 0

    /**
     * @kotlinType Function1<Function0<Int>, Unit> (kotlin.Function1)
     * @subtypes:
     *   [MyClassReceiver.foo1_Functional_Unit],
     *   [foo1_Any_Unit]
     */
    fun foo1_Functional_Unit(f: () -> Int) {}
}


/**
 * @kotlinType [@kotlin.ExtensionFunctionType] Function1<String, Unit> (kotlin.Function1)
 * @subtypes:
 *   [foo1_Any_Unit],
 *   [foo1_CharSequence_Unit]
 */
fun String.foo1_String_Unit() {}

/**
 * @kotlinType [@kotlin.ExtensionFunctionType] Function1<List<Number?>, Unit> (kotlin.Function1)
 * @subtypes:
 *   [foo1_Any_Unit],
 *   [foo1_ListAny_Unit]
 */
fun List<Number?>.foo1_ListNumber_Unit() {}

/**
 * @kotlinType [@kotlin.ExtensionFunctionType] Function1<MyClassReceiver, CharSequence> (kotlin.Function1)
 * @subtypes:
 *   [MyClassReceiver.foo1_MyClassReceiver_String]
 */
fun MyClassReceiver.foo1_MyClass_CharSequence(): CharSequence = "hello"

/**
 * @kotlinType [@kotlin.ExtensionFunctionType] Function2<Number, Int, Int> (kotlin.Function2)
 * @subtypes: no subtypes
 */
fun Number.foo2_Number_Int_Int(int: Int): Int = 0

/**
 * @kotlinType [@kotlin.ExtensionFunctionType] Function2<MyInterface, Array<out Int>, Unit> (kotlin.Function2)
 * @subtypes:
 *   [MyClassReceiver.foo2_Any_Any_Unit],
 *   [foo2_MyInterface_ArrayOutNumber_Unit]
 */
fun MyInterface.foo2_MyInterface_ArrayOutInt_Unit(array: Array<out Int>) {}

/**
 * @kotlinType [@kotlin.ExtensionFunctionType] Function2<MyInterface, Array<out Number>, Unit> (kotlin.Function2)
 * @subtypes:
 *   [MyClassReceiver.foo2_Any_Any_Unit]
 */
fun MyInterface.foo2_MyInterface_ArrayOutNumber_Unit(array: Array<out Number>) {}
