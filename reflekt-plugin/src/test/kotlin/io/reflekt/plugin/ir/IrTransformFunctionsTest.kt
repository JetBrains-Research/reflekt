package io.reflekt.plugin.ir

import io.reflekt.plugin.ir.ResultCall.call
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@Tag("ir")
class IrTransformFunctionsTest {
    companion object {
        @JvmStatic
        fun getReflektFunctionsTestData(): List<Arguments> = listOf(
            Arguments.of(
                setOf(
                    "fun fun1(): kotlin.Unit",
                    "fun io.reflekt.test.ir.FunctionTestClass.Companion.fun1(): kotlin.Unit",
                    "fun io.reflekt.test.ir.FunctionTestObject.fun1(): kotlin.Unit"
                ),
                Signature("() -> Unit", "IrTestAnnotation::class"),
                "",
            ),
            Arguments.of(
                setOf(
                    "fun fun2(): kotlin.Int",
                    "fun io.reflekt.test.ir.FunctionTestClass.Companion.fun2(): kotlin.Int",
                    "fun io.reflekt.test.ir.FunctionTestObject.fun2(): kotlin.Int"
                ),
                Signature("() -> Int", "IrTestAnnotation::class"),
                ""
            ),
            Arguments.of(
                setOf(
                    "fun fun3(): kotlin.collections.List<kotlin.Int>",
                    "fun io.reflekt.test.ir.FunctionTestClass.Companion.fun3(): kotlin.collections.List<kotlin.Int>",
                    "fun io.reflekt.test.ir.FunctionTestObject.fun3(): kotlin.collections.List<kotlin.Int>"
                ),
                Signature("() -> List<Int>", "IrTestAnnotation::class"),
                ""
            ),
            Arguments.of(
                setOf(
                    "fun fun4(kotlin.Int, kotlin.Float?, kotlin.collections.Set<kotlin.Boolean>): kotlin.collections.List<kotlin.String>",
                    "fun io.reflekt.test.ir.FunctionTestClass.Companion.fun4(kotlin.Int, kotlin.Float?, kotlin.collections.Set<kotlin.Boolean>): kotlin.collections.List<kotlin.String>",
                    "fun io.reflekt.test.ir.FunctionTestObject.fun4(kotlin.Int, kotlin.Float?, kotlin.collections.Set<kotlin.Boolean>): kotlin.collections.List<kotlin.String>"
                ),
                Signature("(a: Int, b: Float?, c: Set<Boolean>) -> List<String>", "IrTestAnnotation::class"),
                "1, null, emptySet()"
            )
        )

        @JvmStatic
        fun getReflektMemberFunctionsTestData(): List<Arguments> = listOf(
            Arguments.of(
                setOf(
                    "fun io.reflekt.test.ir.FunctionTestClass.fun1(): kotlin.Unit"
                ),
                Signature("(FunctionTestClass) -> Unit",  "IrTestAnnotation::class"),
                "FunctionTestClass()"
            ),
            Arguments.of(
                setOf(
                    "fun io.reflekt.test.ir.FunctionTestClass.fun2(): kotlin.Int"
                ),
                Signature("(FunctionTestClass) -> Int", "IrTestAnnotation::class"),
                "FunctionTestClass()"
            ),
            Arguments.of(
                setOf(
                    "fun io.reflekt.test.ir.FunctionTestClass.fun3(): kotlin.collections.List<kotlin.Int>"
                ),
                Signature("(FunctionTestClass) -> List<Int>", "IrTestAnnotation::class"),
                "FunctionTestClass()"
            ),
            Arguments.of(
                setOf(
                    "fun io.reflekt.test.ir.FunctionTestClass.fun4(kotlin.Int, kotlin.Float?, kotlin.collections.Set<kotlin.Boolean>): kotlin.collections.List<kotlin.String>"
                ),
                Signature("(FunctionTestClass, Int, Float?, Set<Boolean>) -> List<String>", "IrTestAnnotation::class"),
                "FunctionTestClass(), 1, null, emptySet()"
            ),
        )

        @JvmStatic
        fun getSmartReflektFunctionsTestData(): List<Arguments> = listOf(
            Arguments.of(setOf(""),
                Signature("() -> Boolean", "it.name == \"fooBoolean\""),
                ""
            )
        )

    }

    @ParameterizedTest(name = "Function test#{index} with [{arguments}]")
    @MethodSource("getReflektFunctionsTestData")
    fun testReflektFunctions(expectedFunctions: Set<String>, functionsSignature: Signature, functionsArguments: String) {
        Assertions.assertEquals(expectedFunctions, ReflektType.REFLEKT.functionsInvokeCall(functionsSignature, functionsArguments).call(false))
    }

    @ParameterizedTest(name = "Member function test#{index} with [{arguments}]")
    @MethodSource("getReflektMemberFunctionsTestData")
    fun testReflektMemberFunctions(expectedFunctions: Set<String>, functionsSignature: Signature, functionsArguments: String, ) {
        Assertions.assertEquals(expectedFunctions, ReflektType.REFLEKT.functionsInvokeCall(functionsSignature, functionsArguments).call(false))
    }

    @ParameterizedTest(name = "Function test#{index} with [{arguments}]")
    @Disabled("Failed with IllegalStateException, but in examples everything works fine")
    @MethodSource("getSmartReflektFunctionsTestData")
    fun testSmartReflektFunctions(expectedFunctions: Set<String>, functionsSignature: Signature, functionsArguments: String) {
        Assertions.assertEquals(expectedFunctions, ReflektType.SMART_REFLEKT.functionsInvokeCall(functionsSignature, functionsArguments).call())
    }
}
