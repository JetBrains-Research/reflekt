package io.reflekt.resources.io.reflekt.plugin.analysis.parameterizedtype.functions

typealias MyAlias = Int
interface MyInterface
class MyClass : MyInterface
enum class MyEnum : MyInterface {
    MY_ENUM
}

/**
 * test #11
 * @kotlinType Function0<Unit> (kotlin.Function0)
 * @subtypes:
 *   [MyObjectReceiver.foo0_Unit]
 *   [fun0_Unit_generic]
 */
fun foo0Unit() {}

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
 *   [foo0_MyEnum]
 *   [foo0_MyInterface],
 *   [foo0_Number],
 *   [foo0_String],
 *   [foo0_T],
 *   [foo0_Unit],
 *   [fun0_Unit_generic]
 *
 * @return
 */
fun foo0Any(): Any = 0

/**
 * @kotlinType Function0<String> (kotlin.Function0)
 * @subtypes: no subtypes
 *
 * @return
 */
fun foo0String(): String = "hello"

/**
 * @kotlinType Function0<List<Any>> (kotlin.Function0)
 * @subtypes: no subtypes
 *
 * @return
 */
fun foo0List(): List<Any> = listOf()

/**
 * @kotlinType Function0<Int> (kotlin.Function0)
 * @subtypes:
 *   [foo0_MyAlias]
 *
 * @return
 */
fun foo0Int(): Int = 0

/**
 * @kotlinType Function0<Number> (kotlin.Function0)
 * @subtypes:
 *   [MyClassReceiver.foo0_Number],
 *   [MyObjectReceiver.foo0_Double],
 *   [foo0_Int],
 *   [foo0_MyAlias]
 *
 * @return
 */
fun foo0Number(): Number = 0

/**
 * @kotlinType Function0<Int> (kotlin.Function0)
 * @subtypes:
 *   [foo0_Int]
 *
 * @return
 */
fun foo0MyAlias(): MyAlias = 0

/**
 * @kotlinType Function0<MyInterface> (kotlin.Function0)
 * @subtypes:
 *   [MyGenericClass.foo0_MyGenericClass],
 *   [foo0_MyClass],
 *   [foo0_MyEnum]
 *   [foo0_T]
 *
 * @return
 */
fun foo0MyInterface(): MyInterface = object : MyInterface {}

/**
 * @kotlinType Function0<MyClass> (kotlin.Function0)
 * @subtypes: no subtypes
 *
 * @return
 */
fun foo0MyClass(): MyClass = MyClass()

/**
 * @kotlinType Function0<MyEnum> (kotlin.Function0)
 * @subtypes: no subtypes
 *
 * @return
 */
fun foo0MyEnum(): MyEnum = MyEnum.MY_ENUM

/**
 * @kotlinType Function0<Function0<Unit>> (kotlin.Function0)
 * @subtypes: no subtypes
 *
 * @return
 */
fun foo0Functional(): () -> Unit = { }

/**
 * @kotlinType Function1<CharSequence, Unit> (kotlin.Function1)
 * @subtypes:
 *   [foo1_Any_Unit]
 *
 * @param charSequence
 */
fun foo1CharSequenceUnit(charSequence: CharSequence) {}

/**
 * @kotlinType Function1<Int, Number> (kotlin.Function1)
 * @subtypes:
 *   [MyObjectReceiver.foo1_Int_Number],
 *   [foo1_MyAlias_Number]
 *
 * @param int
 * @return
 */
fun foo1IntNumber(int: Int): Number = 0

/**
 * @kotlinType Function1<Int, Number> (kotlin.Function1)
 * @subtypes:
 *   [MyObjectReceiver.foo1_Int_Number],
 *   [foo1_Int_Number]
 *
 * @param alias
 * @return
 */
fun foo1MyAliasNumber(alias: MyAlias): Number = 0

/**
 * @kotlinType Function1<List<Any?>, Unit> (kotlin.Function1)
 * @subtypes:
 *   [foo1_Any_Unit]
 *
 * @param list
 */
fun foo1ListAnyUnit(list: List<Any?>) {}

/**
 * @kotlinType Function1<List<List<Any>>, Unit> (kotlin.Function1)
 * @subtypes:
 *   [foo1_Any_Unit],
 *   [foo1_ListAny_Unit]
 *
 * @param list
 */
fun foo1ListListAnyUnit(list: List<List<Any>>) {}

/**
 * @kotlinType Function1<Any, Unit> (kotlin.Function1)
 * @subtypes: no subtypes
 *
 * @param any
 */
fun foo1AnyUnit(any: Any) {}

/**
 * @kotlinType Function2<Int, Int, Int> (kotlin.Function2)
 * @subtypes:
 *   [foo2_Number_Int_Int]
 *
 * @param i1
 * @param i2
 * @return
 */
fun foo2IntIntInt(i1: Int, i2: Int): Int = 0
