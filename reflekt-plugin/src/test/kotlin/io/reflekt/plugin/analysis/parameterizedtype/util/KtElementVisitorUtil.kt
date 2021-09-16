package io.reflekt.plugin.analysis.parameterizedtype.util

import io.reflekt.plugin.analysis.*
import io.reflekt.plugin.analysis.common.findReflektFunctionInvokeArguments
import io.reflekt.plugin.util.Util
import io.reflekt.util.FileUtil
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.kdoc.psi.impl.KDocLink
import org.jetbrains.kotlin.kdoc.psi.impl.KDocTag
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import org.jetbrains.kotlin.resolve.BindingContext
import java.io.File
import kotlin.reflect.KClass

fun visitKtElements(sourceFiles: List<File>, visitors: List<KtVisitor<Void, BindingContext>>): BindingContext {
    val reflektClassPath = AnalysisSetupTest.getReflektProjectJars()
    val analyzer =  AnalysisUtil.getBaseAnalyzer(classPath = reflektClassPath, sources = sourceFiles.toSet())
    visitors.forEach { v -> analyzer.ktFiles.forEach { it.acceptChildren(v, analyzer.binding) } }
    return analyzer.binding
}

data class FunctionsToTest(val functions: List<KtNamedFunction>, val binding: BindingContext)

fun getFunctionsToTestFromResources(cls: KClass<*>, testDirName: String): FunctionsToTest {
    val functionFiles = FileUtil.getAllNestedFiles(Util.getResourcesRootPath(cls, testDirName))
    val visitor = KtNamedFunctionVisitor()
    val binding = visitKtElements(functionFiles, listOf(visitor))
    return FunctionsToTest(visitor.functions, binding)
}


/**
 * Collects KtNamedFunctions
 */
class KtNamedFunctionVisitor : KtVisitor<Void, BindingContext>() {
    val functions = mutableListOf<KtNamedFunction>()

    override fun visitNamedFunction(function: KtNamedFunction, data: BindingContext): Void? {
        functions.add(function)
        return super.visitNamedFunction(function, data)
    }

    override fun visitKtElement(element: KtElement, data: BindingContext): Void? {
        element.acceptChildren(this, data)
        return super.visitKtElement(element, data)
    }
}


/**
 * Collects argument types as ASTNodes in CallExpressions together with the expected Kotlin Type written in expression value arguments (see test files),
 * simulating the behaviour of [findReflektFunctionInvokeArguments].
 */
class KtCallExpressionVisitor : KtVisitor<Void, BindingContext>() {
    data class TypeArgument(val astNodeArgument: ASTNode, val stringArgument: String)
    val typeArguments = mutableListOf<TypeArgument>()

    override fun visitCallExpression(expression: KtCallExpression,  data: BindingContext): Void? {
        val typeArgument = expression.node.getTypeArguments().firstOrNull() ?: error("No arguments found in expression $expression")
        val expectedType = expression.valueArguments.firstOrNull()?.text ?: error("No value passed as expected KotlinType in expression $expression")
        // if argument has String type, its text contains extra quotes, so we need to trim them
        typeArguments.add(TypeArgument(typeArgument, expectedType.trim { it == '"' }))
        return super.visitCallExpression(expression, data)
    }

    override fun visitKtElement(element: KtElement, data: BindingContext): Void? {
        element.acceptChildren(this, data)
        return super.visitKtElement(element, data)
    }
}


/**
 * We store all necessary info for tests in functions docs with specific tags (see test files), so we need to get them.
 */
fun KtNamedFunction.findTag(tag: String): KDocTag? {
    return docComment?.getDefaultSection()?.findTagByName(tag)
}

fun KtNamedFunction.getTagContent(tag: String): String {
    return findTag(tag)?.getContent() ?: error("No tag $tag found for function $name")
}

fun KtNamedFunction.parseKDocLinks(tag: String): List<String> {
    return findTag(tag)?.getChildrenOfType<KDocLink>().orEmpty().map { it.getLinkText() }
}
