package io.reflekt.resources.io.reflekt.plugin.analysis.commontestfiles.test

interface AinterfaceTest {
    fun description(): String
}

interface Ainterface1Test : AInterfaceTest {
    override fun description(): String
}

interface BinterfaceTest {
}
