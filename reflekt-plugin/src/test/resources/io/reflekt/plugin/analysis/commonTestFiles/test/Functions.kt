package io.reflekt.test

@SecondAnnotationTest("test")
fun faq() = "/pages/faq|" + "another text"


@FirstAnnotationTest
@SecondAnnotationTest("test")
fun foo() {}

@FirstAnnotationTest
@SecondAnnotationTest("test")
fun fooExpression() = ""

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
