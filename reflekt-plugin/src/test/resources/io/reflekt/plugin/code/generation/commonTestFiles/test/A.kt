package io.reflekt.resources.io.reflekt.plugin.code.generation.commonTestFiles.test

object A1 : AInterfaceTest {
    override fun description(): String = "HELLO A1"
}

@SecondAnnotationTest("Test")
object A2 : AInterfaceTest {
    override fun description(): String = "HELLO A2"
}

@FirstAnnotationTest
object A3 : AInterfaceTest {
    override fun description(): String = "HELLO A3"
}

@SecondAnnotationTest("Test")
object A4 : BInterfaceTest {
}
