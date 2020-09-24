package io.reflekt.plugin.analysis.psi.annotation

import io.reflekt.plugin.utils.compiler.forced
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.resolve.BindingContext

fun KtAnnotationEntry.getDescriptor(context: BindingContext) = context[BindingContext.ANNOTATION, this]!!.forced()

internal val AnnotationDescriptor.qualifiedName: String?
    get() = this.fqName?.asString()
