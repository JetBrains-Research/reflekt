package org.jetbrains.reflekt.plugin.ir.type.util

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.jvm.codegen.psiElement
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.backend.js.utils.asString
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.util.isFakeOverride
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import org.jetbrains.kotlin.kdoc.psi.impl.KDocLink
import org.jetbrains.kotlin.kdoc.psi.impl.KDocTag
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.reflekt.plugin.analysis.ir.*

import java.io.File

/**
 * Registers plugin extension with given visitors to iterate through IR elements of code and perform tests.
 *
 * @property visitors
 */
class IrTestComponentRegistrar(val visitors: List<IrElementVisitor<Unit, IrPluginContext>>) : ComponentRegistrar {
    @ObsoleteDescriptorBasedAPI
    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        IrGenerationExtension.registerExtension(project, object : IrGenerationExtension {
            override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
                println("$moduleFragment module fragment")
                visitors.forEach { moduleFragment.accept(it, pluginContext) }
            }
        })
    }
}

/**
 * We cannot access IR level when compilation is done, so to test it properly we can pass any visitor to it
 * and do any checks during compilation when IR code is generated.
 * After that, we can check the result by getting info from visitors.
 *
 * @param sourceFiles
 * @param visitors
 * @return
 */
fun visitIrElements(sourceFiles: List<File>, visitors: List<IrElementVisitor<Unit, IrPluginContext>>): KotlinCompilation.Result {
    val plugin = IrTestComponentRegistrar(visitors)
    return KotlinCompilation().apply {
        sources = sourceFiles.map { SourceFile.fromPath(it) }
        jvmTarget = "11"
        compilerPlugins = listOf(plugin)
        inheritClassPath = true
        messageOutputStream
        useIR = true
    }.compile()
}

/**
 * Visits IrFunctions, filtered by [filter], for example, to avoid special methods like equals() or toString().
 */
abstract class FilteredIrFunctionVisitor(val filter: (IrFunction) -> Boolean) : IrElementVisitor<Unit, IrPluginContext> {

    abstract fun visitFilteredFunction(declaration: IrFunction, data: IrPluginContext)

    override fun visitFunction(declaration: IrFunction, data: IrPluginContext) {
        if (filter(declaration)) {
            visitFilteredFunction(declaration, data)
        }
        super.visitFunction(declaration, data)
    }

    override fun visitElement(element: IrElement, data: IrPluginContext) = element.acceptChildren(this, data)
}


/**
 * Checks IrType (from expression type arguments) transforming to ParametrizedType, simulating the behaviour of [ReflektFunctionInvokeArgumentsCollector].
 * The expected Kotlin Type is written in expression value arguments.
 */
class IrCallArgumentTypeVisitor : IrElementVisitor<Unit, IrPluginContext> {
    val typeArguments = mutableListOf<TypeArgument>()

    @ObsoleteDescriptorBasedAPI
    override fun visitCall(expression: IrCall, data: IrPluginContext) {
        val typeArgument = expression.getTypeArgument(0) ?: error("No arguments found in expression $expression")
        val type = typeArgument.toParameterizedType()
        val valueArgument = expression.getValueArgument(0) ?: error("No value passed as expected KotlinType in expression $expression")
        val expectedType = (valueArgument as IrConstImpl<*>).value.toString()
        typeArguments.add(TypeArgument(typeArgument.asString(), type, expectedType))
        super.visitCall(expression, data)
    }

    override fun visitElement(element: IrElement, data: IrPluginContext) = element.acceptChildren(this, data)

    /**
     * @property name
     * @property actualType
     * @property expectedType
     */
    data class TypeArgument(
        val name: String,
        val actualType: KotlinType,
        val expectedType: String,
    )
}

/**
 * Visits all functions one by one and checks already visited ones for being a subtype (and vice versa), therefore collecting all the
 * subtype functions among other functions.
 * We filter out fake override functions since they are not implemented in source files and therefore don't have KDoc
 * We have to check subtypes *during* visiting, since [IrPluginContext] is unavailable after compilation is done.
 */
class IrFunctionSubtypesVisitor(namePrefix: String) : FilteredIrFunctionVisitor({ namePrefix in it.name.asString() && !it.isFakeOverride }) {
    val functionSubtypesList: MutableList<FunctionSubtypes> = mutableListOf()

    override fun visitFilteredFunction(declaration: IrFunction, data: IrPluginContext) {
        val declarationSubtypes = FunctionSubtypes(declaration)
        val builtIns = declaration.createIrBuiltIns(data)
        for (functionSubtypes in functionSubtypesList) {
            if (declarationSubtypes.function.isSubtypeOf(functionSubtypes.function, builtIns)) {
                functionSubtypes.actualSubtypes.add(declaration)
            }
            if (functionSubtypes.function.isSubtypeOf(declarationSubtypes.function, builtIns)) {
                declarationSubtypes.actualSubtypes.add(functionSubtypes.function)
            }
        }
        functionSubtypesList.add(declarationSubtypes)

    }


    data class FunctionSubtypes(val function: IrFunction, val actualSubtypes: MutableList<IrFunction> = mutableListOf()) {
        val expectedSubtypes = (function.psiElement as? KtNamedFunction)?.parseKdocLinks("subtypes") ?: emptyList()
    }
}

/**
 * We store all necessary info for tests in functions docs with specific tags (see test files), so we need to get them.
 *
 * @param tag
 * @return
 */
fun KtNamedFunction.findTag(tag: String): KDocTag? = docComment?.getDefaultSection()?.findTagByName(tag)

fun KtNamedFunction.getTagContent(tag: String): String = findTag(tag)?.getContent() ?: error("No tag $tag found for function $name")

fun KtNamedFunction.parseKdocLinks(tag: String): List<String> = findTag(tag)?.getChildrenOfType<KDocLink>().orEmpty().map { it.getLinkText() }
