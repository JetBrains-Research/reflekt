package io.reflekt.plugin.analysis.parameterizedtype.functions

typealias MyAlias = Int
interface MyInterface
class MyClass : MyInterface


/**
 * test #11
 * @kotlinType Function0<Unit> (kotlin.Function0)
 * @subtypes:
 *   [MyObjectReceiver.foo0_Unit]
 *   [fun0_Unit_generic]
 */
fun foo0_Unit() {}

/**
 * @kotlinType Function0<Any> (kotlin.Function0)
 * @subtypes:
 *   [MyObjectReceiver.foo0_Unit]
 *   [MyObjectReceiver.foo0_Double],
 *   [MyClassReceiver.foo0_Number],
 *   [MyGenericClass.foo0_MyGenericClass],
 *   [foo0_Functional],
 *   [foo0_Int],
 *   [foo0_List],
 *   [foo0_MyAlias],
 *   [foo0_MyClass],
 *   [foo0_MyInterface],
 *   [foo0_Number],
 *   [foo0_String],
 *   [foo0_T],
 *   [foo0_Unit],
 *   [fun0_Unit_generic]
 */
fun foo0_Any(): Any = 0

/**
 * @kotlinType Function0<String> (kotlin.Function0)
 * @subtypes: no subtypes
 */
fun foo0_String(): String = "hello"

/**
 * @kotlinType Function0<List<Any>> (kotlin.Function0)
 * @subtypes: no subtypes
 */
fun foo0_List(): List<Any> = listOf()

/**
 * @kotlinType Function0<Int> (kotlin.Function0)
 * @subtypes:
 *   [foo0_MyAlias]
 */
fun foo0_Int(): Int = 0

/**
 * @kotlinType Function0<Number> (kotlin.Function0)
 * @subtypes:
 *   [MyClassReceiver.foo0_Number],
 *   [MyObjectReceiver.foo0_Double],
 *   [foo0_Int],
 *   [foo0_MyAlias]
 */
fun foo0_Number(): Number = 0

/**
 * @kotlinType Function0<Int> (kotlin.Function0)
 * @subtypes:
 *   [foo0_Int]
 */
fun foo0_MyAlias(): MyAlias = 0

/**
 * @kotlinType Function0<MyInterface> (kotlin.Function0)
 * @subtypes:
 *   [MyGenericClass.foo0_MyGenericClass],
 *   [foo0_MyClass],
 *   [foo0_T]
 */
fun foo0_MyInterface(): MyInterface = object : MyInterface {}

/**
 * @kotlinType Function0<MyClass> (kotlin.Function0)
 * @subtypes: no subtypes
 */
fun foo0_MyClass(): MyClass = MyClass()

/**
 * @kotlinType Function0<Function0<Unit>> (kotlin.Function0)
 * @subtypes: no subtypes
 */
fun foo0_Functional(): () -> Unit = { }

/**
 * @kotlinType Function1<CharSequence, Unit> (kotlin.Function1)
 * @subtypes:
 *   [foo1_Any_Unit]
 */
fun foo1_CharSequence_Unit(charSequence: CharSequence) {}

/**
 * @kotlinType Function1<Int, Number> (kotlin.Function1)
 * @subtypes:
 *   [MyObjectReceiver.foo1_Int_Number],
 *   [foo1_MyAlias_Number]
 */
fun foo1_Int_Number(int: Int): Number = 0

/**
 * @kotlinType Function1<Int, Number> (kotlin.Function1)
 * @subtypes:
 *   [MyObjectReceiver.foo1_Int_Number],
 *   [foo1_Int_Number]
 */
fun foo1_MyAlias_Number(alias: MyAlias): Number = 0

/**
 * @kotlinType Function1<List<Any?>, Unit> (kotlin.Function1)
 * @subtypes:
 *   [foo1_Any_Unit]
 */
fun foo1_ListAny_Unit(list: List<Any?>) {}

/**
 * @kotlinType Function1<List<List<Any>>, Unit> (kotlin.Function1)
 * @subtypes:
 *   [foo1_Any_Unit],
 *   [foo1_ListAny_Unit]
 */
fun foo1_ListListAny_Unit(list: List<List<Any>>) {}

/**
 * @kotlinType Function1<Any, Unit> (kotlin.Function1)
 * @subtypes: no subtypes
 */
fun foo1_Any_Unit(any: Any) {}

/**
 * @kotlinType Function2<Int, Int, Int> (kotlin.Function2)
 * @subtypes:
 *   [foo2_Number_Int_Int]
 */
fun foo2_Int_Int_Int(i1: Int, i2: Int): Int = 0
