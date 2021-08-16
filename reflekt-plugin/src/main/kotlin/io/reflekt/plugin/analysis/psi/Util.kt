package io.reflekt.plugin.analysis.psi

import io.reflekt.plugin.analysis.psi.annotation.*
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.synthetics.findClassDescriptor
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.bindingContextUtil.getReferenceTargets
import org.jetbrains.kotlin.resolve.descriptorUtil.*


fun PsiElement.getFqName(binding: BindingContext): String? {
    return (this as? KtExpression)?.getFqName(binding)
}

fun KtExpression.getFqName(binding: BindingContext): String? {
    return getReferenceTargets(binding).singleOrNull()?.fqNameSafe?.asString()
}

/**
 * Checks if a given class or object is subtype of any given [klasses], so its superclasses contain at least one of [klasses].
 */
fun KtClassOrObject.isSubtypeOf(klasses: Set<String>, context: BindingContext): Boolean {
    return findClassDescriptor(context).getAllSuperClassifiers().filter { it is ClassDescriptor }.any {
        it.fqNameOrNull()?.asString() in klasses
    }
}

fun KtClassOrObject.isAnnotatedWith(klasses: Set<String>, context: BindingContext): Boolean {
    return (this as? KtAnnotated)?.getAnnotations(context, klasses)?.size != 0
}

fun KtAnnotationEntry.fqName(context: BindingContext) = getDescriptor(context).qualifiedName

