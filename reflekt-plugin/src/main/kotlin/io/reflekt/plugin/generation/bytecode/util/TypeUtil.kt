package io.reflekt.plugin.generation.bytecode.util

import io.reflekt.plugin.generation.bytecode.FunctionInstanceGenerator
import io.reflekt.plugin.generation.common.ReflektGenerationException
import org.jetbrains.kotlin.codegen.binding.CodegenBinding.ASM_TYPE
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.synthetics.findClassDescriptor
import org.jetbrains.org.objectweb.asm.Type

fun KtClassOrObject.genAsmType(context: ExpressionCodegenExtension.Context): Type =
    context.codegen.bindingContext.get(ASM_TYPE, findClassDescriptor(context.codegen.bindingContext))
        ?: throw ReflektGenerationException("Failed to resolve class [$this]")

fun KtNamedFunction.genAsmType(context: ExpressionCodegenExtension.Context, functionInstanceGenerator: FunctionInstanceGenerator): Type =
    functionInstanceGenerator.generate(this, context)
