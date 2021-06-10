package io.reflekt.plugin.ir

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("ir")
class IrTransformTest {
    @Test
    fun testClasses() {
        assertEquals(
            setOf("io.reflekt.test.ir.C1", "io.reflekt.test.ir.C2", "io.reflekt.test.ir.C3", "io.reflekt.test.ir.C3.C5"),
            IrTransformTestHelper.classFqNames("Reflekt.classes().withSubType<CInterface>()")
        )
    }

    @Test
    fun testObjects() {
        assertEquals(
            setOf("io.reflekt.test.ir.O1", "io.reflekt.test.ir.O1.O2"),
            IrTransformTestHelper.objectFqNames("Reflekt.objects().withSubType<OInterface>()")
        )
    }

    @Test
    fun testFunctions() {
        assertEquals(
            setOf(
                "fun fun1(): kotlin.Unit",
                "fun io.reflekt.test.ir.FunctionTestClass.Companion.fun1(): kotlin.Unit",
                "fun io.reflekt.test.ir.FunctionTestObject.fun1(): kotlin.Unit"
            ),
            IrTransformTestHelper.functionStrings(
                "Reflekt.functions().withAnnotations<() -> Unit>(IrTestAnnotation::class)",
                ""
            )
        )

        assertEquals(
            setOf(
                "fun fun2(): kotlin.Int",
                "fun io.reflekt.test.ir.FunctionTestClass.Companion.fun2(): kotlin.Int",
                "fun io.reflekt.test.ir.FunctionTestObject.fun2(): kotlin.Int"
            ),
            IrTransformTestHelper.functionStrings(
                "Reflekt.functions().withAnnotations<() -> Int>(IrTestAnnotation::class)",
                ""
            )
        )

        assertEquals(
            setOf(
                "fun fun3(): kotlin.collections.List<kotlin.Int>",
                "fun io.reflekt.test.ir.FunctionTestClass.Companion.fun3(): kotlin.collections.List<kotlin.Int>",
                "fun io.reflekt.test.ir.FunctionTestObject.fun3(): kotlin.collections.List<kotlin.Int>"
            ),
            IrTransformTestHelper.functionStrings(
                "Reflekt.functions().withAnnotations<() -> List<Int>>(IrTestAnnotation::class)",
                ""
            )
        )

        assertEquals(
            setOf(
                "fun fun4(kotlin.Int, kotlin.Float?, kotlin.collections.Set<kotlin.Boolean>): kotlin.collections.List<kotlin.String>",
                "fun io.reflekt.test.ir.FunctionTestClass.Companion.fun4(kotlin.Int, kotlin.Float?, kotlin.collections.Set<kotlin.Boolean>): kotlin.collections.List<kotlin.String>",
                "fun io.reflekt.test.ir.FunctionTestObject.fun4(kotlin.Int, kotlin.Float?, kotlin.collections.Set<kotlin.Boolean>): kotlin.collections.List<kotlin.String>"
            ),
            IrTransformTestHelper.functionStrings(
                "Reflekt.functions().withAnnotations<(a: Int, b: Float?, c: Set<Boolean>) -> List<String>>(IrTestAnnotation::class)",
                "1, null, emptySet()"
            )
        )
    }

    @Test
    fun testMemberFunctions() {
        assertEquals(
            setOf("fun io.reflekt.test.ir.FunctionTestClass.fun1(): kotlin.Unit"),
            IrTransformTestHelper.functionStrings(
                "Reflekt.functions().withAnnotations<(FunctionTestClass) -> Unit>(IrTestAnnotation::class)",
                "FunctionTestClass()"
            )
        )

        assertEquals(
            setOf("fun io.reflekt.test.ir.FunctionTestClass.fun2(): kotlin.Int"),
            IrTransformTestHelper.functionStrings(
                "Reflekt.functions().withAnnotations<(FunctionTestClass) -> Int>(IrTestAnnotation::class)",
                "FunctionTestClass()"
            )
        )

        assertEquals(
            setOf("fun io.reflekt.test.ir.FunctionTestClass.fun3(): kotlin.collections.List<kotlin.Int>"),
            IrTransformTestHelper.functionStrings(
                "Reflekt.functions().withAnnotations<(FunctionTestClass) -> List<Int>>(IrTestAnnotation::class)",
                "FunctionTestClass()"
            )
        )

        assertEquals(
            setOf("fun io.reflekt.test.ir.FunctionTestClass.fun4(kotlin.Int, kotlin.Float?, kotlin.collections.Set<kotlin.Boolean>): kotlin.collections.List<kotlin.String>"),
            IrTransformTestHelper.functionStrings(
                "Reflekt.functions().withAnnotations<(FunctionTestClass, Int, Float?, Set<Boolean>) -> List<String>>(IrTestAnnotation::class)",
                "FunctionTestClass(), 1, null, emptySet()"
            )
        )
    }
}
