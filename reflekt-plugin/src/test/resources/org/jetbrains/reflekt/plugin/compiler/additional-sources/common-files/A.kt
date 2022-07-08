package org.jetbrains.reflekt.test.common

object A1 : AInterface {
    override fun description(): String {
        return "HELLO A1"
    }
}

@SecondAnnotation("Test")
object A2 : AInterface {
    override fun description(): String {
        return "HELLO A2"
    }
}

@FirstAnnotation
object A3: AInterface {
    override fun description(): String {
        return "HELLO A3"
    }
}

@SecondAnnotation("Test")
object A4: BInterface

@SecondAnnotation("Test")
private object A5: BInterface
