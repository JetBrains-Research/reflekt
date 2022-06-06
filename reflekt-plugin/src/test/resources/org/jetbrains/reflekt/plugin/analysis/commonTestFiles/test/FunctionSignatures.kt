@file:Suppress("RedundantUnitReturnType", "UNUSED_PARAMETER")

package org.jetbrains.reflekt.test

annotation class FunctionsTestAnnotation

open class FunTest1

open class FunTest2 : FunTest1()

class FunTest3 : FunTest2()

@FunctionsTestAnnotation
fun fun1() {}

@FunctionsTestAnnotation
fun fun2(): Unit {}

@FunctionsTestAnnotation
fun fun3(): Int = 0

@FunctionsTestAnnotation
fun fun4(): String = ""

@FunctionsTestAnnotation
fun fun5(): Array<Int> = emptyArray()

@FunctionsTestAnnotation
fun fun6(): Array<FunTest1> = emptyArray()

@FunctionsTestAnnotation
fun fun7(): Array<FunTest2> = emptyArray()

@FunctionsTestAnnotation
fun fun8(): Array<FunTest3>? = null

@FunctionsTestAnnotation
fun fun9(): Int? = null

@FunctionsTestAnnotation
fun fun10(a: Int, b: Boolean, c: Float): Int = a

@FunctionsTestAnnotation
fun fun11(a: Int, b: Boolean, c: Float): Boolean = b

@FunctionsTestAnnotation
fun fun12(a: Int, b: Boolean, c: Float): Float = c

@FunctionsTestAnnotation
fun fun13(a: List<Int>): List<*> = emptyList<Int>()

@FunctionsTestAnnotation
fun fun14(a: List<Int>): List<Int> = emptyList()

@FunctionsTestAnnotation
fun fun15(a: List<Int>): List<Float> = emptyList()

@FunctionsTestAnnotation
fun fun16(a: List<String>): List<String> = emptyList()

@FunctionsTestAnnotation
fun fun17(a: Array<in FunTest1>): Array<out FunTest2> = emptyArray()

@FunctionsTestAnnotation
fun fun18(a: Array<in FunTest2>): Array<out FunTest2> = emptyArray()

@FunctionsTestAnnotation
fun fun19(a: Array<in FunTest3>): Array<out FunTest2> = emptyArray()
