package io.reflekt.plugin.analysis.parameterizedtype.types

fun main() {
    fooWithType<MyTypeAlias<String>>("List<String> (kotlin.collections.List)")

    fooWithType<MyType>("MyType (io.reflekt.plugin.analysis.parameterizedtype.types.MyType)")

    fooWithType<List<MyInheritedType>>("List<MyInheritedType> (kotlin.collections.List)")

    fooWithType<Array<out MyGenericType<String>>>("Array<out MyGenericType<String>> (kotlin.Array)")

    fooWithType<MyObject>("MyObject (io.reflekt.plugin.analysis.parameterizedtype.types.MyObject)")

    fooWithType<MyEnumType>("MyEnumType (io.reflekt.plugin.analysis.parameterizedtype.types.MyEnumType)")
}
