package io.reflekt.plugin.analysis.psi

import io.reflekt.plugin.analysis.processor.Processor
import org.jetbrains.kotlin.psi.*


// TODO: rename??
fun KtElement.visit(processors: Set<Processor<*>>) {
    acceptChildren(object : KtDefaultVisitor() {
        override fun visitObjectDeclaration(declaration: KtObjectDeclaration) {
            processors.filter { it.shouldRunOn(declaration) }.forEach { it.process(declaration) }
            super.visitObjectDeclaration(declaration)
        }

        override fun visitClass(klass: KtClass) {
            processors.filter { it.shouldRunOn(klass) }.forEach { it.process(klass) }
            super.visitClass(klass)
        }

        override fun visitReferenceExpression(expression: KtReferenceExpression) {
            processors.filter { it.shouldRunOn(expression) }.forEach { it.process(expression) }
            super.visitReferenceExpression(expression)
        }
    })
}
