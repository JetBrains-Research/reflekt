package org.jetbrains.reflekt.plugin.ir.type

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.util.parentClassOrNull
import org.jetbrains.reflekt.plugin.analysis.psi.function.toParameterizedType
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.typeUtil.isSubtypeOf
import org.jetbrains.reflekt.plugin.analysis.ir.irType
import org.jetbrains.reflekt.plugin.analysis.ir.isSubTypeOf
import org.jetbrains.reflekt.plugin.ir.type.util.*
import org.jetbrains.reflekt.plugin.util.Util
import org.jetbrains.reflekt.util.file.getAllNestedFiles
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class FunctionSubtypesTest {
    private fun IrFunction.getNameWithClass(): String {
        val parent =  this.parentClassOrNull
//        todo: maybe fix later
//        var classOrObject = getParentOfType<KtClassOrObject>(true)
//        if (classOrObject is KtObjectDeclaration && classOrObject.isCompanion()) {
//            classOrObject = classOrObject.getParentOfType<KtClass>(true)
//        }
        val className = parent?.name?.let { "$it." } ?: ""
        return this.name.let { "$className$it" }
    }

    @Tag("ir")
    @MethodSource("getIrFunctionsWithSubtypes")
    @ParameterizedTest(name = "test {index}")
    fun testFunctionSubtypes(
        functionSubtypes: IrFunctionSubtypesVisitor.FunctionSubtypes,
    ) {
        Assertions.assertEquals(
            functionSubtypes.expectedSubtypes.sorted(),
            functionSubtypes.actualSubtypes.map { it.getNameWithClass() }.sorted(),
            "Incorrect subtypes for function ${functionSubtypes.function.name} ${functionSubtypes.irType}",
        )
    }
    companion object {
        private const val TEST_DIR_NAME = "functions"
        private const val FUNCTION_PREFIX = "foo"

        @JvmStatic
        fun getIrFunctionsWithSubtypes(): List<Arguments> {
            val files = Util.getResourcesRootPath(FunctionSubtypesTest::class, TEST_DIR_NAME).getAllNestedFiles()
            val visitor = IrFunctionSubtypesVisitor { FUNCTION_PREFIX in it }
            visitIrElements(files, listOf(visitor))
            return visitor.functionSubtypesList.map { Arguments.of(it) }
        }
    }
}
