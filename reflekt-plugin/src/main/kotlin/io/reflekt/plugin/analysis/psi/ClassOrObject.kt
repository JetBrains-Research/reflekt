package io.reflekt.plugin.analysis.psi

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.impl.FunctionExpressionDescriptor
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.children
import org.jetbrains.kotlin.psi.psiUtil.isFunctionalExpression
import org.jetbrains.kotlin.psi.synthetics.findClassDescriptor
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.bindingContextUtil.getDataFlowInfoAfter
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.resolve.descriptorUtil.getAllSuperClassifiers
import org.jetbrains.kotlin.resolve.bindingContextUtil.getReferenceTargets
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import kotlin.reflect.KClass

fun KtElement.visitClassOrObject(filter: (KtClassOrObject) -> Boolean = { true }, body: (KtClassOrObject) -> Unit) {
    acceptChildren(object : KtDefaultVisitor() {
        override fun visitClassOrObject(classOrObject: KtClassOrObject) {
            if (filter(classOrObject)) body(classOrObject)

            super.visitClassOrObject(classOrObject)
        }
    })
}

fun KtElement.visitClass(filter: (KtClass) -> Boolean = { true }, body: (KtClass) -> Unit) {
    acceptChildren(object : KtDefaultVisitor() {
        override fun visitClass(klass: KtClass) {
            if (filter(klass)) body(klass)

            super.visitClass(klass)
        }
    })
}

fun KtElement.visitObject(filter: (KtObjectDeclaration) -> Boolean = { true }, body: (KtObjectDeclaration) -> Unit) {
    acceptChildren(object : KtDefaultVisitor() {
        override fun visitObjectDeclaration(declaration: KtObjectDeclaration) {
            if (filter(declaration)) body(declaration)

            super.visitObjectDeclaration(declaration)
        }
    })
}

fun KtElement.visitKtDotQualifiedExpression(context: BindingContext) {
    acceptChildren(object : KtDefaultVisitor() {
        override fun visitElement(element: PsiElement) {
            if (element.node.text == "withSubType") {
                println("visitElement " + element.node.text + " " + element.getFqName(context))
            }
            super.visitElement(element)
        }
    })
}

fun KtClassOrObject.isSubtypeOf(klasses: Set<String>, context: BindingContext): Boolean {
    return findClassDescriptor(context).getAllSuperClassifiers().filter { it is ClassDescriptor }.any {
        it.fqNameOrNull()?.asString() in klasses
    }
}

fun KtExpression.getFqName(binding: BindingContext): String? {
    return getReferenceTargets(binding).singleOrNull()?.fqNameSafe?.asString()
}

fun KtReferenceExpression.getFqName(binding: BindingContext): String? {
    return getReferenceTargets(binding).singleOrNull()?.fqNameSafe?.asString()
}

fun PsiElement.getFqName(binding: BindingContext): String? {
    return (this as? KtExpression)?.getReferenceTargets(binding).toString()
}
