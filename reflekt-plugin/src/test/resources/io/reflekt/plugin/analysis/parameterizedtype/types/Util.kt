package io.reflekt.plugin.analysis.parameterizedtype.types


/**
 * Function to test IrType or AstNode -> KotlinType, expected KotlinType is passed as argument
 */
fun <T> fooWithType(kotlinType: String) { }

typealias MyTypeAlias<T> = List<T>

interface MyInterfaceType

class MyType

class MyInheritedType : MyInterfaceType

class MyGenericType<T : CharSequence>

object MyObject
