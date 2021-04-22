package io.reflekt.example

@FirstAnnotation
fun foo() {
    println("public first example foo")
}

@FirstAnnotation
private fun barPrivate() {
    println("private first example bar")
}

@FirstAnnotation
fun bar() {
    println("public first example bar")
}

class TestFunctions {
    @FirstAnnotation
    fun foo() {
        println("public first example foo in TestFunctions class")
    }

    @FirstAnnotation
    private fun barPrivate() {
        println("private first example bar in TestFunctions class")
    }

    companion object {
        @FirstAnnotation
        fun foo() {
            println("public first example foo in companion object in TestFunctions class")
        }
    }
}
