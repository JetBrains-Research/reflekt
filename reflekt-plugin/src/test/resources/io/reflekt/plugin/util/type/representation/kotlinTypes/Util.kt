package io.reflekt.resources.io.reflekt.plugin.util.type.representation.kotlinTypes

typealias MyTypeAlias<T> = List<T>

class MyClass

class MyGenericType<T : CharSequence>

object MyObject

interface MyInterfaceType

class MyInheritedType : MyInterfaceType

enum class MyEnumType : MyInterfaceType {
    MY_ENUM
}

/**
 * Function to test a string representation of KotlinTypes, a test key is passed as argument
 *
 * @param testKey
 */
fun <T> fooWithType(testKey: String) { }
