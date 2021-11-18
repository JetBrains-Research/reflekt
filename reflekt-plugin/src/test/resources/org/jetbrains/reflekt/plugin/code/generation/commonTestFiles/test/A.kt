package org.jetbrains.reflekt.codegen.test

object A1: AInterfaceTest {
    override fun description(): String {
        return "HELLO A1"
    }
}

@SecondAnnotationTest("Test")
object A2: AInterfaceTest {
    override fun description(): String {
        return "HELLO A2"
    }
}

@FirstAnnotationTest
object A3: AInterfaceTest {
    override fun description(): String {
        return "HELLO A3"
    }
}

@SecondAnnotationTest("Test")
object A4: BInterfaceTest {
}
