package org.jetbrains.reflekt.test.common

class B1: BInterface

@FirstAnnotation
@SecondAnnotation("Test", FirstAnnotation(int = 42, array = [false, true]))
data class B2(val x: Int): BInterface

@FirstAnnotation
@SecondAnnotation("Test")
class B3: BInterface {
    class B4: BInterface
}

private data class B4(val x: Double): BInterface
