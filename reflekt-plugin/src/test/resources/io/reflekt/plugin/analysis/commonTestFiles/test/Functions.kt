package io.reflekt.test

@FirstAnnotationTest
fun foo() {}

@FirstAnnotationTest
private fun barPrivate() {}

@FirstAnnotationTest
fun bar() {}

class TestFunctions {
    @FirstAnnotationTest
    fun foo() {}

    @FirstAnnotationTest
    private fun barPrivate() {}

    companion object {
        @FirstAnnotationTest
        fun foo() {}
    }
}
