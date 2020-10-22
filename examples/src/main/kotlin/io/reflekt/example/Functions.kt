package io.reflekt.example

@FirstAnnotation
fun foo() {}

@FirstAnnotation
private fun barPrivate() {}

@FirstAnnotation
fun bar() {}

class TestFunctions {
    @FirstAnnotation
    fun foo() {}

    @FirstAnnotation
    private fun barPrivate() {}

    companion object {
        @FirstAnnotation
        fun foo() {}
    }
}
