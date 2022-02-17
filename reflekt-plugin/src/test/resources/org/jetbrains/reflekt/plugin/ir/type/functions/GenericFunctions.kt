package org.jetbrains.reflekt.plugin.ir.type.functions


/**
 * @kotlinType Function0<Unit> (kotlin.Function0)
 * @subtypes:
 *  cannot infer the type T -> not subtypes
 */
fun <T> fun0_Unit_generic() {}


/**
 * @kotlinType Function1<T, Number> (kotlin.Function1)
 * @subtypes:
 *  [foo1_MyAlias_Number]
 *  [foo1_Int_Number]
 */
fun <T : Number> foo1_TNumber_Number(n: T): Number = 0

/**
 * @kotlinType Function1<T, Unit> (kotlin.Function1)
 * @subtypes:
 */
fun <T> foo1_T_Unit(t: T) {}

/**
 * @kotlinType Function0<T> (kotlin.Function0)
 * @subtypes: no subtypes
 */
inline fun <reified T : MyInterface> foo0_T(): T = TODO()


