package io.reflekt.plugin.analysis.parameterizedtype

import io.reflekt.plugin.analysis.parameterizedtype.util.KtCallExpressionVisitor
import io.reflekt.plugin.analysis.parameterizedtype.util.visitKtElements
import io.reflekt.plugin.analysis.toParameterizedType
import io.reflekt.plugin.analysis.toPrettyString
import io.reflekt.plugin.util.Util
import io.reflekt.util.file.getAllNestedFiles
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.resolve.BindingContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class AstNodeToParameterizedTypeTest {
    companion object {
        private const val TEST_DIR_NAME = "types"

        @JvmStatic
        fun getAstNodeKotlinTypes(): List<Arguments> {
            val files = Util.getResourcesRootPath(AstNodeToParameterizedTypeTest::class, TEST_DIR_NAME).getAllNestedFiles()
            val visitor = KtCallExpressionVisitor()
            val binding = visitKtElements(files, listOf(visitor))
            return visitor.typeArguments.map { Arguments.of(binding, it.astNodeArgument, it.stringArgument) }
        }
    }

    @Tag("parametrizedType")
    @MethodSource("getAstNodeKotlinTypes")
    @ParameterizedTest(name = "test {index}")
    fun testAstNodeToParameterizedType(binding: BindingContext, astNode: ASTNode, expectedType: String) {
        Assertions.assertEquals(expectedType, astNode.toParameterizedType(binding).toPrettyString(), "Incorrect type for ASTNode ${astNode.text}")
    }
}
