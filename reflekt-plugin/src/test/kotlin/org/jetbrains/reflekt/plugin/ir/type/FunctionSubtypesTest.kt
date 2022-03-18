package org.jetbrains.reflekt.plugin.ir.type

import org.jetbrains.reflekt.plugin.analysis.toPrettyString
import org.jetbrains.reflekt.plugin.ir.type.util.IrFunctionSubtypesVisitor
import org.jetbrains.reflekt.plugin.ir.type.util.visitIrElements
import org.jetbrains.reflekt.plugin.util.Util
import org.jetbrains.reflekt.util.file.getAllNestedFiles
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class FunctionSubtypesTest {
    @Tag("ir")
    @MethodSource("getIrFunctionsWithSubtypes")
    @ParameterizedTest(name = "test {index}")
    fun testFunctionSubtypes(
        functionSubtypes: IrFunctionSubtypesVisitor.FunctionSubtypes,
    ) {
        Assertions.assertEquals(
            functionSubtypes.expectedSubtypes.sorted(),
            functionSubtypes.actualSubtypes.map { it.toPrettyString() }.sorted(),
            "Incorrect subtypes for function ${functionSubtypes.function.toPrettyString()}",
        )
    }


    companion object {
        private const val TEST_DIR_NAME = "functions"
        private const val FUNCTION_PREFIX = "foo"

        @JvmStatic
        fun getIrFunctionsWithSubtypes(): List<Arguments> {
            val files = Util.getResourcesRootPath(FunctionSubtypesTest::class, TEST_DIR_NAME).getAllNestedFiles()
            val visitor = IrFunctionSubtypesVisitor(FUNCTION_PREFIX)
            visitIrElements(files, listOf(visitor))
            return visitor.functionSubtypesList.map { Arguments.of(it) }
        }
    }
}
