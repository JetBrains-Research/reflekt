package org.jetbrains.reflekt.test.ir

interface CInterface

open class C1 : CInterface

open class C2 : C1()

open class C3 : C2() {
    class C5 : CInterface
}
