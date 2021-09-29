package org.jetbrains.reflekt.plugin.analysis.parameterizedtype

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.reflekt.plugin.analysis.ir.ReflektFunctionInvokeArgumentsCollector
import org.jetbrains.reflekt.plugin.analysis.ir.toParameterizedType
import org.jetbrains.reflekt.plugin.analysis.parameterizedtype.util.getTagContent
import org.jetbrains.reflekt.plugin.analysis.toPrettyString
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
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.KotlinType
import java.io.File

/**
 * We cannot access IR level when compilation is done, so to test it properly we can pass any visitor to it
 * and do any checks during compilation when IR code is generated.
 * After that, we can check the result by getting info from visitors.
 */
fun visitIrElements(sourceFiles: List<File>, visitors: List<IrElementVisitor<Unit, BindingContext>>): KotlinCompilation.Result {
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

class IrTestComponentRegistrar(val visitors: List<IrElementVisitor<Unit, BindingContext>>) : ComponentRegistrar {

    @ObsoleteDescriptorBasedAPI
    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        IrGenerationExtension.registerExtension(project, object : IrGenerationExtension {
            override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
                println("$moduleFragment module fragment")
                visitors.forEach { moduleFragment.accept(it, pluginContext.bindingContext) }
            }
        })
    }
}

/**
 * Checks IrFunctions transforming to ParameterizedType, stores the result and expected Kotlin Type which is written in docs.
 * [filterByName] is used to avoid visiting special methods like equals() or toString()
 */
class IrFunctionTypeVisitor(val filterByName: (String) -> Boolean) : IrElementVisitor<Unit, BindingContext> {
    data class Function(val name: String, val actualType: KotlinType, val expectedType: String)
    val functions = mutableListOf<Function>()

    override fun visitFunction(declaration: IrFunction, data: BindingContext) {
        val name = declaration.name.asString()
        if (filterByName(name)) {
            val type = declaration.toParameterizedType(data) ?: error("Kotlin type of function $name is null")
            val expectedType = (declaration.psiElement as? KtNamedFunction)?.getTagContent("kotlinType") ?: ("Expected kotlin type of function $name is null")
            functions.add(Function(name, type, expectedType))
        }
        super.visitFunction(declaration, data)
    }

    override fun visitElement(element: IrElement, data: BindingContext) {
        return element.acceptChildren(this, data)
    }
}

/**
 * Checks IrType (from expression type arguments) transforming to ParametrizedType, simulating the behaviour of [ReflektFunctionInvokeArgumentsCollector].
 * The expected Kotlin Type is written in expression value arguments.
 */
class IrCallArgumentTypeVisitor : IrElementVisitor<Unit, BindingContext> {
    data class TypeArgument(val name: String, val actualType: KotlinType, val expectedType: String)
    val typeArguments = mutableListOf<TypeArgument>()

    @ObsoleteDescriptorBasedAPI
    override fun visitCall(expression: IrCall, data: BindingContext) {
        val typeArgument = expression.getTypeArgument(0) ?: error("No arguments found in expression $expression")
        val type = typeArgument.toParameterizedType()
        val valueArgument = expression.getValueArgument(0) ?: error("No value passed as expected KotlinType in expression $expression")
        val expectedType = (valueArgument as IrConstImpl<*>).value.toString()
        typeArguments.add(TypeArgument(typeArgument.asString(), type, expectedType))
        println(type.toPrettyString())
        super.visitCall(expression, data)
    }

    override fun visitElement(element: IrElement, data: BindingContext) {
        return element.acceptChildren(this, data)
    }
}
