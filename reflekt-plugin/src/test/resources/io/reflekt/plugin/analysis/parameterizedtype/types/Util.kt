package io.reflekt.resources.io.reflekt.plugin.analysis.parameterizedtype.types

typealias MyTypeAlias<T> = List<T>

interface MyInterfaceType

class MyType

class MyInheritedType : MyInterfaceType

class MyGenericType<T : CharSequence>

object MyObject

enum class MyEnumType : MyInterfaceType {
    MY_ENUM
}

/**
 * Function to test IrType or AstNode -> KotlinType, expected KotlinType is passed as argument
 *
 * @param kotlinType
 */
fun <T> fooWithType(kotlinType: String) { }
