package io.reflekt.resources.io.reflekt.plugin.ir.commontestfiles.test

interface Cinterface

open class C1 : CInterface

open class C2 : C1()

open class C3 : C2() {
    class C5 : CInterface
}
