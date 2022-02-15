package org.jetbrains.reflekt.plugin.ir.type

import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.reflekt.plugin.ir.type.util.*
import org.jetbrains.reflekt.plugin.util.Util
import org.jetbrains.reflekt.util.file.getAllNestedFiles
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.*

class FunctionSubtypesTest {
    private fun IrFunction.getNameWithClass() = "${this.parentAsClass.name}.${this.name}"

    @Tag("ir")
    @MethodSource("getIrFunctionsWithSubtypes")
    @ParameterizedTest(name = "test {index}")
    fun testFunctionSubtypes(
        functionSubtypes: IrFunctionSubtypesVisitor.FunctionSubtypes,
    ) {
        Assertions.assertEquals(
            functionSubtypes.expectedSubtypes.sorted(),
            functionSubtypes.actualSubtypes.map { it.getNameWithClass() }.sorted(),
            "Incorrect subtypes for function ${functionSubtypes.function.getNameWithClass()}",
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
