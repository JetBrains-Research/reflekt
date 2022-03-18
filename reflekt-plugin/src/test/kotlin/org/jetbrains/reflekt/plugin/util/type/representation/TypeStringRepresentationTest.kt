package org.jetbrains.reflekt.plugin.util.type.representation

import org.jetbrains.reflekt.util.file.getAllNestedFiles
import org.jetbrains.reflekt.plugin.analysis.parameterizedtype.util.KtCallExpressionVisitor
import org.jetbrains.reflekt.plugin.analysis.parameterizedtype.util.visitKtElements
import org.jetbrains.reflekt.plugin.analysis.toParameterizedType
import org.jetbrains.reflekt.plugin.util.Util.getResourcesRootPath
import org.jetbrains.reflekt.plugin.utils.stringRepresentation
import org.jetbrains.reflekt.util.stringRepresentation
import org.jetbrains.kotlin.types.KotlinType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import org.jetbrains.reflekt.plugin.analysis.readTextNormalized

class TypeStringRepresentationTest {
    @Tag("codegen")
    @MethodSource("data")
    @ParameterizedTest(name = "test {index} {2}")
    fun `types string representation test`(
        kType: KType,
        kotlinType: KotlinType,
        expectedStringRepresentation: String) {
        // TODO: can we use classes for KType from the resources folder?
        val resourcePackageName = "org.jetbrains.reflekt.plugin.util.type.representation.kotlinTypes"
        val srcPackageName = "org.jetbrains.reflekt.plugin.util.type.representation"

        val kTypeStr = kType.stringRepresentation().replace(srcPackageName, resourcePackageName)
        Assertions.assertEquals(expectedStringRepresentation, kTypeStr, "Incorrect string representation for KType $kType")
        val kotlinTypeStr: String = kotlinType.stringRepresentation()
        Assertions.assertEquals(expectedStringRepresentation, kotlinTypeStr, "Incorrect string representation for KotlinType $kotlinType")
    }

    companion object {
        @OptIn(ExperimentalStdlibApi::class)
        private val test_data = mapOf(
            "function0_unit_test" to (typeOf<() -> Unit>()),
            "function0_simple_type_test" to (typeOf<() -> Int>()),
            "function0_user_type_test" to (typeOf<() -> MyClass>()),
            "function0_list_test" to (typeOf<() -> List<Any>>()),
            "function0_user_alias_type_test" to (typeOf<() -> MyTypeAlias<Any>>()),
            "function0_generic_simple_type_test" to (typeOf<() -> MyGenericType<String>>()),
            "function0_generic_with_in_test" to (typeOf<() -> MyGenericType<in String>>()),
            "function0_generic_with_out_test" to (typeOf<() -> MyGenericType<out String>>()),
            "function0_user_object_test" to (typeOf<() -> MyObject>()),
            "function0_inherited_type_test" to (typeOf<() -> MyInheritedType>()),
            "function0_star_type_test" to (typeOf<() -> List<*>>()),
            "function0_same_type_with_bound_test" to (typeOf<() -> Any?>()),
            "function0_generic_star_type_test" to (typeOf<() -> MyGenericType<*>>()),

            // TODO: will be fixed in 1.6.0-M1 version
            // "function0_complex_type_test" to (typeOf<() -> MutableCollection<List<Array<Any>>>>()),
            // TODO: it will be fixed in 1.6.0-M1 version
            // "function0_nothing_test" to (typeOf<() -> Nothing>()),

            "function0_unit_nullable_test" to (typeOf<() -> Unit?>()),
            "function0_simple_type_nullable_test" to (typeOf<() -> Int?>()),
            "function0_user_type_nullable_test" to (typeOf<() -> MyClass?>()),
            "function0_list_nullable_all_test" to (typeOf<() -> List<Any?>?>()),
            "function0_list_nullable_test" to (typeOf<() -> List<Any>?>()),
            "function0_list_nullable_argument_test" to (typeOf<() -> List<Any?>>()),
            "function0_user_alias_type_nullable_test" to (typeOf<() -> MyTypeAlias<Any?>?>()),
            "function0_generic_simple_type_nullable_test" to (typeOf<() -> MyGenericType<String>?>()),
            "function0_generic_with_out_nullable_test" to (typeOf<() -> MyGenericType<out String>?>()),
            "function0_user_object_nullable_test" to (typeOf<() -> MyObject?>()),
            "function0_inherited_type_nullable_test" to (typeOf<() -> MyInheritedType?>()),
            "function0_star_type_nullable_test" to (typeOf<() -> List<*>?>()),
            "function0_generic_star_type_nullable_test" to (typeOf<() -> MyGenericType<*>?>()),

            "function1_test" to (typeOf<(List<Set<Any?>>?) -> List<*>>()),
            "function2_test" to (typeOf<(Int, Iterable<*>) -> Unit>()),
        )

        private fun getResultForTest(files: Set<File>, testKey: String): String {
            val resFile = files.find { it.nameWithoutExtension.endsWith(testKey) } ?: error("File with results for the test $testKey was not found")
            return resFile.readTextNormalized()
        }

        @OptIn(ExperimentalStdlibApi::class)
        @JvmStatic
        fun data(): List<Arguments> {
            val resFilesSet = getResourcesRootPath(TypeStringRepresentationTest::class, "results").getAllNestedFiles().toSet()
            val kotlinTypesFiles = getResourcesRootPath(TypeStringRepresentationTest::class, "kotlinTypes").getAllNestedFiles()
            val visitor = KtCallExpressionVisitor()
            val binding = visitKtElements(kotlinTypesFiles, listOf(visitor))
            val testKeyToKotlinTypeMap = visitor.typeArguments.associate { it.stringArgument to it.astNodeArgument.toParameterizedType(binding) }
            return test_data.map { (testKey, kType) ->
                Arguments.of(
                    kType,
                    testKeyToKotlinTypeMap[testKey] ?: error("KotlinType for the test $testKey was not found"),
                    getResultForTest(resFilesSet, testKey),
                )
            }
        }
    }
}
