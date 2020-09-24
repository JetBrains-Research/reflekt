package io.reflekt.plugin.analysis.psi.annotation

import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.resolve.BindingContext
import kotlin.reflect.KClass

fun KtAnnotationEntry.fqName(context: BindingContext) = getDescriptor(context).qualifiedName

inline fun <reified T : Annotation> KtAnnotated.isAnnotatedWith(context: BindingContext) = getAnnotations<T>(context).isNotEmpty()
inline fun <reified T : Annotation> KtAnnotated.getAnnotations(context: BindingContext) = getAnnotations(context, T::class)

fun KtAnnotated.getAnnotations(context: BindingContext, annotations: Set<KClass<*>>) = annotationEntries.filter {
    it.fqName(context) in annotations.map { ann -> ann.qualifiedName }.toSet()
}.toSet()

fun KtAnnotated.getAnnotations(context: BindingContext, ann: KClass<*>) = getAnnotations(context, setOf(ann))
