package io.reflekt.resources.io.reflekt.plugin.analysis.parameterizedtype.functions

class MyGenericClass <T : Number> : MyInterface {
    /**
     * @kotlinType [@kotlin.ExtensionFunctionType] Function2<MyGenericClass<T>, T, Unit> (kotlin.Function2)
     * @subtypes:
     *   [MyClassReceiver.foo2_Any_Any_Unit]
     *
     * @param t
     */
    fun foo2MyGenericClassTnumberUnit(t: T) {}

    /**
     * @kotlinType [@kotlin.ExtensionFunctionType] Function2<MyGenericClass<T>, S, Unit> (kotlin.Function2)
     * @subtypes:
     *   [MyClassReceiver.foo2_Any_Any_Unit]
     *
     * @param s
     */
    fun <S : CharSequence> foo2MyGenericClassScharSequenceUnit(s: S) {}

    companion object {
        /**
         * @kotlinType Function0<MyGenericClass<S>> (kotlin.Function0)
         * @subtypes: no subtypes
         *
         * @return
         */
        fun <S : Number> foo0MyGenericClass() = MyGenericClass<S>()
    }
}

object MyObject {
    /**
     * @kotlinType Function2<S, T, Unit> (kotlin.Function2)
     * @subtypes: no subtypes
     *
     * @param s
     * @param t
     */
    fun <S, T> foo2Stunit(s: S, t: T) {}
}

/**
 * @kotlinType [@kotlin.ExtensionFunctionType] Function5<T, N, A, C, S, Unit> (kotlin.Function5)
 * @subtypes: no subtypes
 *
 * @param n
 * @param a
 * @param c
 * @param s
 */
fun <T, N : Number, A : Any?, C : CharSequence?, S> T.foo5Tnacsunit(
    n: N,
    a: A,
    c: C,
    s: S) {}

/**
 * @kotlinType Function0<Unit> (kotlin.Function0)
 * @subtypes:
 *   [MyObjectReceiver.foo0_Unit],
 *   [foo0_Unit]
 */
fun <T> fun0UnitGeneric() {}

/**
 * @kotlinType Function1<T, Number> (kotlin.Function1)
 * @subtypes: no subtypes
 *
 * @param n
 * @return
 */
fun <T : Number> foo1TnumberNumber(n: T): Number = 0

/**
 * @kotlinType Function1<T, Unit> (kotlin.Function1)
 * @subtypes: no subtypes
 *
 * @param t
 */
fun <T> foo1Tunit(t: T) {}

/**
 * @kotlinType Function5<Any, Number, Any, CharSequence, Any, Unit> (kotlin.Function5)
 * @subtypes: no subtypes
 *
 * @param a1
 * @param n
 * @param a2
 * @param c
 * @param s
 */
fun foo5AnyNumberAnyCharSequenceAnyUnit(
    a1: Any,
    n: Number,
    a2: Any,
    c: CharSequence,
    s: Any) {}

/**
 * @kotlinType Function0<T> (kotlin.Function0)
 * @subtypes: no subtypes
 *
 * @return
 */
inline fun <reified T : MyInterface> foo0T(): T = TODO()
