package org.jetbrains.reflekt.test.common

@FirstAnnotation
fun foo2() {
    println("public first example foo")
}

@FirstAnnotation
private fun foo3() {
    println("private first example bar")
}

@FirstAnnotation
fun foo4() {
    println("public first example bar")
}

class TestFunctions {
    @FirstAnnotation
    fun foo5() {
        println("public first example foo in TestFunctions class")
    }

    @FirstAnnotation
    private fun foo6() {
        println("private first example bar in TestFunctions class")
    }

    companion object {
        @FirstAnnotation
        fun foo7() {
            println("public first example foo in companion object in TestFunctions class")
        }
    }
}

fun fooBoolean(): Boolean {
    println("public second example foo")
    return true
}

inline fun <reified T> fooArray(): Array<T> = emptyArray()

fun <T> fooList(): List<T> = emptyList()

class MyInClass<in T>

fun <T> fooMyInClass(): MyInClass<T> = MyInClass()

fun withStar(a: List<*>) { }

fun <T: Number> withBound(a: T) { println("withBound function, a: $a") }
