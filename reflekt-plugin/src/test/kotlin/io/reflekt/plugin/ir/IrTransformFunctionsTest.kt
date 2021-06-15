package io.reflekt.plugin.ir

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@Tag("ir")
class IrTransformFunctionsTest {
    companion object {
        @JvmStatic
        fun getFunctionsTestData(): List<Arguments> = listOf(
            Arguments.of(
                setOf(
                    "fun fun1(): kotlin.Unit",
                    "fun io.reflekt.test.ir.FunctionTestClass.Companion.fun1(): kotlin.Unit",
                    "fun io.reflekt.test.ir.FunctionTestObject.fun1(): kotlin.Unit"
                ),
                "Reflekt.functions().withAnnotations<() -> Unit>(IrTestAnnotation::class)",
                ""
            ),
            Arguments.of(
                setOf(
                    "fun fun2(): kotlin.Int",
                    "fun io.reflekt.test.ir.FunctionTestClass.Companion.fun2(): kotlin.Int",
                    "fun io.reflekt.test.ir.FunctionTestObject.fun2(): kotlin.Int"
                ),
                "Reflekt.functions().withAnnotations<() -> Int>(IrTestAnnotation::class)",
                ""
            ),
            Arguments.of(
                setOf(
                    "fun fun3(): kotlin.collections.List<kotlin.Int>",
                    "fun io.reflekt.test.ir.FunctionTestClass.Companion.fun3(): kotlin.collections.List<kotlin.Int>",
                    "fun io.reflekt.test.ir.FunctionTestObject.fun3(): kotlin.collections.List<kotlin.Int>"
                ),
                "Reflekt.functions().withAnnotations<() -> List<Int>>(IrTestAnnotation::class)",
                ""
            ),
            Arguments.of(
                setOf(
                    "fun fun4(kotlin.Int, kotlin.Float?, kotlin.collections.Set<kotlin.Boolean>): kotlin.collections.List<kotlin.String>",
                    "fun io.reflekt.test.ir.FunctionTestClass.Companion.fun4(kotlin.Int, kotlin.Float?, kotlin.collections.Set<kotlin.Boolean>): kotlin.collections.List<kotlin.String>",
                    "fun io.reflekt.test.ir.FunctionTestObject.fun4(kotlin.Int, kotlin.Float?, kotlin.collections.Set<kotlin.Boolean>): kotlin.collections.List<kotlin.String>"
                ),
                "Reflekt.functions().withAnnotations<(a: Int, b: Float?, c: Set<Boolean>) -> List<String>>(IrTestAnnotation::class)",
                "1, null, emptySet()"
            )
        )

        @JvmStatic
        fun getMemberFunctionsTestData(): List<Arguments> = listOf(
            Arguments.of(
                setOf(
                    "fun io.reflekt.test.ir.FunctionTestClass.fun1(): kotlin.Unit"
                ),
                "Reflekt.functions().withAnnotations<(FunctionTestClass) -> Unit>(IrTestAnnotation::class)",
                "FunctionTestClass()"
            ),
            Arguments.of(
                setOf(
                    "fun io.reflekt.test.ir.FunctionTestClass.fun2(): kotlin.Int"
                ),
                "Reflekt.functions().withAnnotations<(FunctionTestClass) -> Int>(IrTestAnnotation::class)",
                "FunctionTestClass()"
            ),
            Arguments.of(
                setOf(
                    "fun io.reflekt.test.ir.FunctionTestClass.fun3(): kotlin.collections.List<kotlin.Int>"
                ),
                "Reflekt.functions().withAnnotations<(FunctionTestClass) -> List<Int>>(IrTestAnnotation::class)",
                "FunctionTestClass()"
            ),
            Arguments.of(
                setOf(
                    "fun io.reflekt.test.ir.FunctionTestClass.fun4(kotlin.Int, kotlin.Float?, kotlin.collections.Set<kotlin.Boolean>): kotlin.collections.List<kotlin.String>"
                ),
                "Reflekt.functions().withAnnotations<(FunctionTestClass, Int, Float?, Set<Boolean>) -> List<String>>(IrTestAnnotation::class)",
                "FunctionTestClass(), 1, null, emptySet()"
            ),
        )
    }

    private fun executeFunctionTest(expectedFunctions: Set<String>, reflektCall: String, functionCallArguments: String) {
        Assertions.assertEquals(
            expectedFunctions,
            IrTransformTestHelper.functionStrings(
                reflektCall,
                functionCallArguments
            )
        )
    }

    @ParameterizedTest(name = "Function test#{index} with [{arguments}]")
    @MethodSource("getFunctionsTestData")
    fun testFunctions(expectedFunctions: Set<String>, reflektCall: String, functionCallArguments: String) {
        executeFunctionTest(expectedFunctions, reflektCall, functionCallArguments)
    }

    @ParameterizedTest(name = "Member function test#{index} with [{arguments}]")
    @MethodSource("getMemberFunctionsTestData")
    fun testMemberFunctions(expectedFunctions: Set<String>, reflektCall: String, functionCallArguments: String) {
        executeFunctionTest(expectedFunctions, reflektCall, functionCallArguments)
    }
}
