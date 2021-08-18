package io.reflekt.plugin.ir

import io.reflekt.plugin.ir.ResultCall.call
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("ir")
class IrTransformClassesAndObjectsTest {
    @Test
    fun testClasses() {
        assertEquals(
            setOf("io.reflekt.test.ir.C1", "io.reflekt.test.ir.C2", "io.reflekt.test.ir.C3", "io.reflekt.test.ir.C3.C5"),
            ReflektType.REFLEKT.classesFqNamesCall(Signature("CInterface")).call(false)
        )
    }

    @Test
    fun testObjects() {
        assertEquals(
            setOf("io.reflekt.test.ir.O1", "io.reflekt.test.ir.O1.O2"),
            ReflektType.REFLEKT.objectsFqNamesCall(Signature("OInterface")).call(false)
        )
    }
}
