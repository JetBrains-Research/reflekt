package io.reflekt.plugin.analysis.parameterizedtype.types

fun main() {
    fooWithType<() -> Unit>("Function0<Unit> (kotlin.Function0)")

    fooWithType<(String, Any) -> Nothing>("Function2<String, Any, Nothing> (kotlin.Function2)")

    fooWithType<(MyType) -> MyType>("Function1<MyType, MyType> (kotlin.Function1)")

    fooWithType<(MyTypeAlias<Any>) -> MyGenericType<*>>("Function1<List<Any>, MyGenericType<*>> (kotlin.Function1)")

    fooWithType<() -> MutableCollection<List<Array<Any>>>>("Function0<MutableCollection<List<Array<Any>>>> (kotlin.Function0)")

    fooWithType<(MyInterfaceType) -> String>("Function1<MyInterfaceType, String> (kotlin.Function1)")

    fooWithType<(MyInheritedType) -> MyGenericType<String>>("Function1<MyInheritedType, MyGenericType<String>> (kotlin.Function1)")

    fooWithType<(MyObject) -> Number>("Function1<MyObject, Number> (kotlin.Function1)")
}
