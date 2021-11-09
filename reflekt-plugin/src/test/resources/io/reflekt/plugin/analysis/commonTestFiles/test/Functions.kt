package io.reflekt.resources.io.reflekt.plugin.analysis.commonTestFiles.test

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
fun foo() {}

@FirstAnnotationTest
@SecondAnnotationTest("test")
fun bar() {}

@FirstAnnotationTest
@SecondAnnotationTest("test")
private fun barPrivate() {}
