package org.jetbrains.reflekt.plugin.analysis.psi

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.synthetics.findClassDescriptor
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.bindingContextUtil.getReferenceTargets
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.reflekt.plugin.analysis.psi.annotation.*
import org.jetbrains.reflekt.plugin.analysis.resolve.isSubtypeOf

fun PsiElement.getFqName(binding: BindingContext): String? = (this as? KtExpression)?.getFqName(binding)

fun KtExpression.getFqName(binding: BindingContext) = getReferenceTargets(binding).singleOrNull()?.fqNameSafe?.asString()

/**
 * Checks if a given class or object is subtype of any given [klasses], so its superclasses contain at least one of the [klasses].
 *
 * @param klasses
 * @param context
 * @return true if a class/object is a subtype of any of [klasses]
 */
fun KtClassOrObject.isSubtypeOf(klasses: Set<String>, context: BindingContext): Boolean = findClassDescriptor(context).isSubtypeOf(klasses)

fun KtClassOrObject.isAnnotatedWith(klasses: Set<String>, context: BindingContext) = (this as? KtAnnotated)?.getAnnotations(context, klasses)?.size != 0

fun KtAnnotationEntry.fqName(context: BindingContext) = getDescriptor(context).qualifiedName
