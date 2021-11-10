package io.reflekt.plugin.analysis.parameterizedtype

import io.reflekt.plugin.analysis.toPrettyString
import io.reflekt.plugin.util.Util
import io.reflekt.util.file.getAllNestedFiles

import com.tschuchort.compiletesting.KotlinCompilation
import io.reflekt.plugin.analysis.parameterizedtype.util.IrCallArgumentTypeVisitor
import io.reflekt.plugin.analysis.parameterizedtype.util.visitIrElements
import org.jetbrains.kotlin.types.KotlinType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class IrTypeToParameterizedTypeTest {
    @Tag("parametrizedType")
    @MethodSource("getIrTypeKotlinTypes")
    @ParameterizedTest(name = "test {index}")
    fun testIrTypeToParameterizedType(
        name: String,
        actualType: KotlinType,
        expectedKotlinType: String?,
        exitCode: KotlinCompilation.ExitCode) {
        Assertions.assertEquals(KotlinCompilation.ExitCode.OK, exitCode)
        Assertions.assertEquals(expectedKotlinType, actualType.toPrettyString(), "Incorrect type for function $name")
    }
    companion object {
        private const val TEST_DIR_NAME = "types"

        @JvmStatic
        fun getIrTypeKotlinTypes(): List<Arguments> {
            val files = Util.getResourcesRootPath(IrTypeToParameterizedTypeTest::class, TEST_DIR_NAME).getAllNestedFiles()
            val visitor = IrCallArgumentTypeVisitor()
            val exitCode = visitIrElements(files, listOf(visitor)).exitCode
            return visitor.typeArguments.map { Arguments.of(it.name, it.actualType, it.expectedType, exitCode) }
        }
    }
}
