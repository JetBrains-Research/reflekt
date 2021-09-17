package io.reflekt.plugin.ir

import io.reflekt.plugin.ir.ResultCall.call
import org.gradle.internal.impldep.org.junit.Ignore
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("ir")
@Ignore("In examples it works correctly, " +
    "but in tests got the error: java.lang.NoSuchMethodError: " +
    "java.lang.String org.jetbrains.kotlin.ir.util.DumpIrTreeKt.dump#default")
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
