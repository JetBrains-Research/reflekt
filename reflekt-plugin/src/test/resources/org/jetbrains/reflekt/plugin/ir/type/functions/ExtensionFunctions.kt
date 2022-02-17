package org.jetbrains.reflekt.plugin.ir.type.functions


/**
 * @kotlinType [@kotlin.ExtensionFunctionType] Function1<String, Unit> (kotlin.Function1)
 * @subtypes:
 *  [CharSequence.foo1_CharSequence_Unit]
 *  [Any.extension_foo1_Any_Unit]
 */
fun String.foo1_String_Unit() {}


/**
 * @kotlinType [@kotlin.ExtensionFunctionType] Function1<String, Unit> (kotlin.Function1)
 * @subtypes:
 *  [Any.extension_foo1_Any_Unit]
 */
fun CharSequence.foo1_CharSequence_Unit() {}

/**
 * @kotlinType [@kotlin.ExtensionFunctionType] Function1<List<Number?>, Unit> (kotlin.Function1)
 * @subtypes:
 *   [Any.extension_foo1_Any_Unit],
 */
fun List<Number?>.foo1_ListNumber_Unit() {}

/**
 * @kotlinType [@kotlin.ExtensionFunctionType] Function1<Any, Unit> (kotlin.Function1)
 * @subtypes:
 *
 */
fun Any.extension_foo1_Any_Unit() {}


/**
 * @kotlinType [@kotlin.ExtensionFunctionType] Function2<Number, Int, Any> (kotlin.Function2)
 * @subtypes: [Any.foo2_Number_Int_Int]
 */
fun Number.foo2_Number_Int_Any(int: Int): Any = 0

/**
 * @kotlinType [@kotlin.ExtensionFunctionType] Function2<Any, Int, Int> (kotlin.Function2)
 * @subtypes:
 */
fun Any.foo2_Number_Int_Int(int: Int): Int = 0


