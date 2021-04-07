package io.reflekt.example

class B1: BInterface {
}

@FirstAnnotation
@SecondAnnotation("Test")
data class B2(val x: Int): BInterface

@FirstAnnotation
@SecondAnnotation("Test")
class B3: BInterface {
    class B4: BInterface
}
