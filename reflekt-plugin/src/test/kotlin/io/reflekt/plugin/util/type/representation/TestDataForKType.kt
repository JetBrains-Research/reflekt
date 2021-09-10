package io.reflekt.plugin.util.type.representation

internal class MyClass

internal typealias MyTypeAlias<T> = List<T>

internal class MyGenericType<T : CharSequence>

internal object MyObject

internal interface MyInterfaceType

internal enum class MyEnumType : MyInterfaceType { MY_ENUM }

internal class MyInheritedType : MyInterfaceType
