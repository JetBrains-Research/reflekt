package io.reflekt.example

fun foo() {
    println("public second example foo")
}

private fun barPrivate() {
    println("private second example bar")
}

fun bar() {
    println("public second example bar")
}

class TestFunctions {
    fun foo() {
        println("public second example foo in TestFunctions class")
    }

    private fun barPrivate() {
        println("private second example bar in TestFunctions class")
    }

    companion object {
        fun foo() {
            println("public second example foo in companion object in TestFunctions class")
        }
    }
}
