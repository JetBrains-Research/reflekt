package org.jetbrains.reflekt.codegen.test

@FirstAnnotationTest
@SecondAnnotationTest("test")
fun foo() {}

@FirstAnnotationTest
@SecondAnnotationTest("test")
private fun barPrivate() {}

@FirstAnnotationTest
@SecondAnnotationTest("test")
fun bar() {}

class TestFunctions {
    @FirstAnnotationTest
    fun foo() {}

    @FirstAnnotationTest
    @SecondAnnotationTest("test")
    private fun barPrivate() {}

    companion object {
        @FirstAnnotationTest
        @SecondAnnotationTest("test")
        fun foo() {}
    }
}

@FirstAnnotationTest
@SecondAnnotationTest("test")
fun fooWithReturnedValue(): Int { return 5 }
