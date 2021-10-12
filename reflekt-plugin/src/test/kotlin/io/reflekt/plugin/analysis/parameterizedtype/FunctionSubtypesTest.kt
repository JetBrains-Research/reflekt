package io.reflekt.plugin.analysis.parameterizedtype

import io.reflekt.plugin.analysis.parameterizedtype.util.getFunctionsToTestFromResources
import io.reflekt.plugin.analysis.parameterizedtype.util.parseKDocLinks
import io.reflekt.plugin.analysis.psi.function.toParameterizedType
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.typeUtil.isSubtypeOf
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class FunctionSubtypesTest {
    companion object {
        private const val TEST_DIR_NAME = "functions"

        @JvmStatic
        fun getKtNamedFunctionsWithSubtypes(): List<Arguments> {
            val (functions, binding) = getFunctionsToTestFromResources(FunctionSubtypesTest::class, TEST_DIR_NAME)
            return functions.map { Arguments.of(binding, it, functions.removeFromList(it), it.parseKDocLinks("subtypes")) }
        }

        private fun <T> List<T>.removeFromList(toRemove: T): List<T> {
            val functions = this.toMutableList()
            functions.remove(toRemove)
            return functions
        }
    }

    private fun KtNamedFunction.getNameWithClass(): String {
        var classOrObject = getParentOfType<KtClassOrObject>(true)
        if (classOrObject is KtObjectDeclaration && classOrObject.isCompanion()) {
            classOrObject = classOrObject.getParentOfType<KtClass>(true)
        }
        val className = classOrObject?.name?.let { "$it." } ?: ""
        return this.name?.let { "$className$it" } ?: error("Name of function $this is null")
    }

    @Tag("parametrizedType")
    @MethodSource("getKtNamedFunctionsWithSubtypes")
    @ParameterizedTest(name = "test {index}")
    fun testFunctionSubtypes(binding: BindingContext, function: KtNamedFunction, otherFunctions: List<KtNamedFunction>, expectedSubtypes: List<String>) {
        val functionType = function.toParameterizedType(binding) ?: error("KotlinType of function ${function.name} is null")
        val actualSubtypes = otherFunctions.filter { it.toParameterizedType(binding)?.isSubtypeOf(functionType) == true }
        Assertions.assertEquals(
            expectedSubtypes.sorted(),
            actualSubtypes.map { it.getNameWithClass() }.sorted(),
            "Incorrect subtypes for function ${function.name} $functionType"
        )
    }
}
