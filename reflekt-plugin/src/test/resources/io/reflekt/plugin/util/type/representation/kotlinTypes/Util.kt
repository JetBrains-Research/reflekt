package io.reflekt.plugin.util.type.representation.kotlinTypes



/**
 * Function to test a string representation of KotlinTypes, a test key is passed as argument
 */
fun <T> fooWithType(testKey: String) { }

class MyClass

typealias MyTypeAlias<T> = List<T>

class MyGenericType<T : CharSequence>

object MyObject

interface MyInterfaceType

class MyInheritedType : MyInterfaceType

enum class MyEnumType : MyInterfaceType { MY_ENUM }
