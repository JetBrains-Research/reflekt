package io.reflekt.test.ir

@IrTestAnnotation
fun fun1() {}

@IrTestAnnotation
fun fun2(): Int = 3

@IrTestAnnotation
fun fun3(): List<Int> = listOf(3)

@IrTestAnnotation
fun fun4(a: Int, b: Float?, c: Set<Boolean>): List<String> = listOf(a.toString(), b.toString(), c.toString())

fun fooBoolean(): Boolean {
    println("public second example foo")
    return true
}

class FunctionTestClass {
    @IrTestAnnotation
    fun fun1() {}

    @IrTestAnnotation
    fun fun2(): Int = 3

    @IrTestAnnotation
    fun fun3(): List<Int> = listOf(3)

    @IrTestAnnotation
    fun fun4(a: Int, b: Float?, c: Set<Boolean>): List<String> = listOf(a.toString(), b.toString(), c.toString())

    companion object {
        @IrTestAnnotation
        fun fun1() {}

        @IrTestAnnotation
        fun fun2(): Int = 3

        @IrTestAnnotation
        fun fun3(): List<Int> = listOf(3)

        @IrTestAnnotation
        fun fun4(a: Int, b: Float?, c: Set<Boolean>): List<String> = listOf(a.toString(), b.toString(), c.toString())
    }
}

object FunctionTestObject {
    @IrTestAnnotation
    fun fun1() {}

    @IrTestAnnotation
    fun fun2(): Int = 3

    @IrTestAnnotation
    fun fun3(): List<Int> = listOf(3)

    @IrTestAnnotation
    fun fun4(a: Int, b: Float?, c: Set<Boolean>): List<String> = listOf(a.toString(), b.toString(), c.toString())
}
