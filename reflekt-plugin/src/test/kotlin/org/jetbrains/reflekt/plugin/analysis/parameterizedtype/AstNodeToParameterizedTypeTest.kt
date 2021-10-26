package org.jetbrains.reflekt.plugin.analysis.parameterizedtype

import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.reflekt.plugin.analysis.parameterizedtype.util.KtCallExpressionVisitor
import org.jetbrains.reflekt.plugin.analysis.parameterizedtype.util.visitKtElements
import org.jetbrains.reflekt.plugin.analysis.toParameterizedType
import org.jetbrains.reflekt.plugin.analysis.toPrettyString
import org.jetbrains.reflekt.plugin.util.Util
import org.jetbrains.reflekt.util.FileUtil
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
            val files = FileUtil.getAllNestedFiles(Util.getResourcesRootPath(AstNodeToParameterizedTypeTest::class, TEST_DIR_NAME))
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
