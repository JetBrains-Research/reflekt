package org.jetbrains.reflekt.plugin.ir.type.functions



class MyGenericClass <T: Number> : MyInterface {

    /**
     * @kotlinType [@kotlin.ExtensionFunctionType] Function2<MyGenericClass<T>, T, Unit> (kotlin.Function2)
     * @subtypes: no subtypes
     */
    fun foo2_MyGenericClass_TNumber_Unit(t: T) {}

    /**
     * @kotlinType [@kotlin.ExtensionFunctionType] Function2<MyGenericClass<T>, S, Unit> (kotlin.Function2)
     * @subtypes: no subtypes
     */
    fun <S : CharSequence> foo2_MyGenericClass_SCharSequence_Unit(s: S) {}

    companion object {
        /**
         * @kotlinType Function0<MyGenericClass<S>> (kotlin.Function0)
         * @subtypes: no subtypes
         */
        fun <S : Number> foo0_MyGenericClass() = MyGenericClass<S>()
    }
}

object MyObject {
    /**
     * @kotlinType Function2<S, T, Unit> (kotlin.Function2)
     * @subtypes: no subtypes
     */
    fun <S, T> foo2_S_T_Unit(s: S, t: T) {}
}


/**
 * @kotlinType Function0<Unit> (kotlin.Function0)
 * @subtypes:
 *   [foo0_Unit]
 */
fun <T> fun0_Unit_generic() {}

/**
 * @kotlinType Function1<T, Number> (kotlin.Function1)
 * @subtypes: no subtypes
 */
fun <T : Number> foo1_TNumber_Number(n: T): Number = 0

/**
 * @kotlinType Function1<T, Unit> (kotlin.Function1)
 * @subtypes: no subtypes
 */
fun <T> foo1_T_Unit(t: T) {}

/**
 * @kotlinType [@kotlin.ExtensionFunctionType] Function5<T, N, A, C, S, Unit> (kotlin.Function5)
 * @subtypes: no subtypes
 */
fun <T, N : Number, A : Any?, C : CharSequence?, S> T.foo5_T_N_A_C_S_Unit(n: N, a: A, c: C, s: S) {}

/**
 * @kotlinType Function5<Any, Number, Any, CharSequence, Any, Unit> (kotlin.Function5)
 * @subtypes: no subtypes
 */
fun foo5_Any_Number_Any_CharSequence_Any_Unit(a1: Any, n: Number, a2: Any, c: CharSequence, s: Any) {}

/**
 * @kotlinType Function0<T> (kotlin.Function0)
 * @subtypes: no subtypes
 */
inline fun <reified T : MyInterface> foo0_T(): T = TODO()
