package io.reflekt.plugin.analysis.psi

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.synthetics.findClassDescriptor
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.bindingContextUtil.getReferenceTargets
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.getAllSuperClassifiers


fun PsiElement.getFqName(binding: BindingContext): String? {
    return (this as? KtExpression)?.getFqName(binding)
}

fun KtClassOrObject.isSubtypeOf(klasses: Set<String>, context: BindingContext): Boolean {
    return findClassDescriptor(context).getAllSuperClassifiers().filter { it is ClassDescriptor }.any {
        it.fqNameOrNull()?.asString() in klasses
    }
}

fun KtClassOrObject.isAnnotatedWith(klasses: Set<String>, context: BindingContext): Boolean {
    return (this as? KtAnnotated)?.annotations?.any{
        it.getFqName(context) in klasses
    } ?: false
}

fun KtExpression.getFqName(binding: BindingContext): String? {
    return getReferenceTargets(binding).singleOrNull()?.fqNameSafe?.asString()
}

