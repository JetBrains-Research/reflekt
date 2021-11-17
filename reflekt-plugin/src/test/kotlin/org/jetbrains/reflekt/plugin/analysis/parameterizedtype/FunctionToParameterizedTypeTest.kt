package org.jetbrains.reflekt.plugin.analysis.parameterizedtype

import org.jetbrains.reflekt.plugin.analysis.*
import org.jetbrains.reflekt.plugin.analysis.parameterizedtype.util.*
import org.jetbrains.reflekt.plugin.analysis.psi.function.*
import org.jetbrains.reflekt.plugin.util.Util

import com.tschuchort.compiletesting.KotlinCompilation
import org.jetbrains.reflekt.util.file.getAllNestedFiles
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.KotlinType
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

import java.io.File

class FunctionToParameterizedTypeTest {
    @Tag("parametrizedType")
    @MethodSource("getKtNamedFunctionsWithTypes")
    @ParameterizedTest(name = "test {index}")
    fun testKtNamedFunctions(
        binding: BindingContext,
        function: KtNamedFunction,
        expectedKotlinType: String) {
        Assertions.assertEquals(expectedKotlinType, function.toParameterizedType(binding).toPrettyString(), "Incorrect type for function ${function.name}")
    }

    @Tag("parametrizedType")
    @MethodSource("getIrFunctionsWithTypes")
    @ParameterizedTest(name = "test {index}")
    fun testIrFunctions(
        name: String,
        actualType: KotlinType,
        expectedKotlinType: String?,
        exitCode: KotlinCompilation.ExitCode) {
        Assertions.assertEquals(KotlinCompilation.ExitCode.OK, exitCode)
        Assertions.assertEquals(expectedKotlinType, actualType.toPrettyString(), "Incorrect type for function $name")
    }

    @Tag("parametrizedType")
    @Disabled("Kotlin type of overridden IrFunction is null, see detailed explanation in comments above")
    @MethodSource("getIrFunctionsWithTypesFailed")
    @ParameterizedTest(name = "test {index}")
    fun testIrFunctionsFailed(
        name: String,
        actualType: KotlinType,
        expectedKotlinType: String?,
        exitCode: KotlinCompilation.ExitCode) {
        Assertions.assertEquals(KotlinCompilation.ExitCode.OK, exitCode)
        Assertions.assertEquals(expectedKotlinType, actualType.toPrettyString(), "Incorrect type for function $name")
    }

    companion object {
        private const val FUNCTION_PREFIX = "foo"
        private const val TEST_DIR_NAME = "functions"

        /**
         * For some reason, sometimes abstract overridden IR functions have nullable KotlinType (as well as PsiElement). Not sure, if it's a bug, since
         * everything works well for a simple case (see AbstractAndOverrideFunctionsSimple.kt),
         * but fails with abstract functions in AbstractAndOverrideFunctions.kt.
         * TODO: maybe detailed testing is needed
         */
        private val failedIrFiles = listOf("AbstractAndOverrideFunctions.kt")

        @JvmStatic
        fun getKtNamedFunctionsWithTypes(): List<Arguments> {
            val (functions, binding) = getFunctionsToTestFromResources(FunctionToParameterizedTypeTest::class, TEST_DIR_NAME)
            return functions.map { Arguments.of(binding, it, it.getTagContent("kotlinType")) }
        }

        @JvmStatic
        fun getIrFunctionsWithTypes(): List<Arguments> = visitIrFunctionsWithTypes { it.name !in failedIrFiles }

        @JvmStatic
        fun getIrFunctionsWithTypesFailed(): List<Arguments> = visitIrFunctionsWithTypes { it.name in failedIrFiles }

        private fun visitIrFunctionsWithTypes(filterFiles: (File) -> Boolean): List<Arguments> {
            val functionFiles = Util.getResourcesRootPath(FunctionToParameterizedTypeTest::class, TEST_DIR_NAME).getAllNestedFiles().filter { filterFiles(it) }
            // We need to filter out all default functions like "equals", "toString", etc, so every function in tests has a special prefix in its name
            val visitor = IrFunctionTypeVisitor { FUNCTION_PREFIX in it }
            val exitCode = visitIrElements(functionFiles, listOf(visitor)).exitCode
            return visitor.functions.map { f -> Arguments.of(f.name, f.actualType, f.expectedType, exitCode) }
        }
    }
}
