package org.jetbrains.reflekt.test

class B1: BInterfaceTest {
}

@FirstAnnotationTest
@SecondAnnotationTest("Test")
class B2: BInterfaceTest {
}

@FirstAnnotationTest
@SecondAnnotationTest("Test")
class B3: BInterfaceTest {
    class B4: BInterfaceTest
}
