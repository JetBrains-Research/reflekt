package org.jetbrains.reflekt.plugin.analysis.parameterizedtype

import com.tschuchort.compiletesting.KotlinCompilation
import org.jetbrains.reflekt.plugin.analysis.toPrettyString
import org.jetbrains.reflekt.plugin.util.Util
import org.jetbrains.reflekt.util.FileUtil
import org.jetbrains.kotlin.types.KotlinType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class IrTypeToParameterizedTypeTest {
    companion object {
        private const val TEST_DIR_NAME = "types"

        @JvmStatic
        fun getIrTypeKotlinTypes(): List<Arguments> {
            val files = FileUtil.getAllNestedFiles(Util.getResourcesRootPath(IrTypeToParameterizedTypeTest::class, TEST_DIR_NAME))
            val visitor = IrCallArgumentTypeVisitor()
            val exitCode = visitIrElements(files, listOf(visitor)).exitCode
            return visitor.typeArguments.map { Arguments.of(it.name, it.actualType, it.expectedType, exitCode) }
        }
    }

    @Tag("parametrizedType")
    @MethodSource("getIrTypeKotlinTypes")
    @ParameterizedTest(name = "test {index}")
    fun testIrTypeToParameterizedType(name: String, actualType: KotlinType, expectedKotlinType: String?, exitCode: KotlinCompilation.ExitCode) {
        Assertions.assertEquals(KotlinCompilation.ExitCode.OK, exitCode)
        Assertions.assertEquals(expectedKotlinType, actualType.toPrettyString(), "Incorrect type for function $name")
    }
}