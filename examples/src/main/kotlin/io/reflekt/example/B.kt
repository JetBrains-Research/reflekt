package io.reflekt.example

class B1: BInterface {
}

@FirstAnnotation
@SecondAnnotation("Test")
class B2: BInterface {
}

@FirstAnnotation
@SecondAnnotation("Test")
class B3: BInterface {
    class B4: BInterface
}
