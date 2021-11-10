package io.reflekt.test

interface AInterfaceTest {
    fun description(): String
}

interface AInterface1Test: AInterfaceTest {
    override fun description(): String
}

interface BInterfaceTest {
}
