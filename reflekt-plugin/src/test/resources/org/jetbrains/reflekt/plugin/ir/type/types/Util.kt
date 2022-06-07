package org.jetbrains.reflekt.plugin.ir.type.types


/**
 * Function to test IrType or AstNode -> KotlinType, expected KotlinType is passed as an argument
 */
fun <T> fooWithType(kotlinType: String) { }

typealias MyTypeAlias<T> = List<T>

interface MyInterfaceType

class MyType

class MyInheritedType : MyInterfaceType

class MyGenericType<T : CharSequence>

object MyObject

enum class MyEnumType : MyInterfaceType { MY_ENUM }
