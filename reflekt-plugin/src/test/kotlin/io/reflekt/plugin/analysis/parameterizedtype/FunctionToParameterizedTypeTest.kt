package io.reflekt.plugin.analysis.parameterizedtype

import com.tschuchort.compiletesting.KotlinCompilation
import io.reflekt.plugin.analysis.*
import io.reflekt.plugin.analysis.parameterizedtype.util.*
import io.reflekt.plugin.analysis.psi.function.*
import io.reflekt.plugin.util.Util
import io.reflekt.util.FileUtil.getAllNestedFiles
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.KotlinType
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File

class FunctionToParameterizedTypeTest {

    companion object {
        /**
         * For some reason, sometimes abstract overridden IR functions have nullable KotlinType (as well as PsiElement). Not sure, if it's a bug, since
         * everything works well for a simple case (see AbstractAndOverrideFunctionsSimple.kt),
         * but fails with abstract functions in AbstractAndOverrideFunctions.kt.
         * TODO: maybe detailed testing is needed
         */
        private val failedIrFiles = listOf("AbstractAndOverrideFunctions.kt")
        private val testDirName = "functions"

        @JvmStatic
        fun getKtNamedFunctionsWithTypes(): List<Arguments> {
            val functionFiles = getAllNestedFiles(Util.getResourcesRootPath(FunctionToParameterizedTypeTest::class, testDirName))
            val visitor = KtNamedFunctionVisitor()
            val binding = visitKtElements(functionFiles, listOf(visitor))
            val functions = visitor.functions
            return functions.map { Arguments.of(binding, it, it.getTagContent("kotlinType")) }
        }

        @JvmStatic
        fun getIrFunctionsWithTypes(): List<Arguments> {
            val functionFiles = getAllNestedFiles(Util.getResourcesRootPath(FunctionToParameterizedTypeTest::class, testDirName)).filter { it.name !in failedIrFiles }
            val (functions, exitCode) = visitIrFunctionsWithTypes(functionFiles)
            return functions.map { f -> Arguments.of(f.name, f.actualType, f.expectedType, exitCode) }
        }

        @JvmStatic
        fun getIrFunctionsWithTypesFailed(): List<Arguments> {
            val functionFiles = getAllNestedFiles(Util.getResourcesRootPath(FunctionToParameterizedTypeTest::class, testDirName)).filter { it.name in failedIrFiles }
            val (functions, exitCode) = visitIrFunctionsWithTypes(functionFiles)
            return functions.map { f -> Arguments.of(f.name, f.actualType, f.expectedType, exitCode) }
        }

        private fun visitIrFunctionsWithTypes(files: List<File>): FunctionsWithExitCode {
            // We need to filter out all default functions like "equals", "toString", etc, so every function in tests has "foo" in its name
            val visitor = IrFunctionTypeVisitor { "foo" in it }
            val exitCode = visitIrElements(files, listOf(visitor)).exitCode
            return visitor.functions to exitCode
        }
    }


    @Tag("analysis")
    @MethodSource("getKtNamedFunctionsWithTypes")
    @ParameterizedTest(name = "test {index}")
    fun testKtNamedFunctions(binding: BindingContext, function: KtNamedFunction, expectedKotlinType: String) {
        Assertions.assertEquals(expectedKotlinType, function.toParameterizedType(binding).toPrettyString(), "Incorrect type for function ${function.name}")
    }

    @Tag("analysis")
    @MethodSource("getIrFunctionsWithTypes")
    @ParameterizedTest(name = "test {index}")
    fun testIrFunctions(name: String, actualType: KotlinType, expectedKotlinType: String?, exitCode: KotlinCompilation.ExitCode) {
        Assertions.assertEquals(KotlinCompilation.ExitCode.OK, exitCode)
        Assertions.assertEquals(expectedKotlinType, actualType.toPrettyString(), "Incorrect type for function $name")
    }

    @Tag("analysis")
    @Disabled("Kotlin type of overridden IrFunction is null, see detailed explanation in comments above")
    @MethodSource("getIrFunctionsWithTypesFailed")
    @ParameterizedTest(name = "test {index}")
    fun testIrFunctionsFailed(name: String, actualType: KotlinType, expectedKotlinType: String?, exitCode: KotlinCompilation.ExitCode) {
        Assertions.assertEquals(KotlinCompilation.ExitCode.OK, exitCode)
        Assertions.assertEquals(expectedKotlinType, actualType.toPrettyString(), "Incorrect type for function $name")
    }
}
