package io.reflekt.plugin.analysis.psi

import io.reflekt.plugin.analysis.processor.source.Processor
import org.jetbrains.kotlin.psi.*


fun KtFile.visit(processors: Set<Processor<*>>) {
    acceptChildren(object : KtDefaultVisitor() {
        override fun visitObjectDeclaration(declaration: KtObjectDeclaration) {
            processors.filter { it.shouldRunOn(declaration) }.forEach { it.process(declaration, this@visit) }
            super.visitObjectDeclaration(declaration)
        }

        override fun visitClass(klass: KtClass) {
            processors.filter { it.shouldRunOn(klass) }.forEach { it.process(klass, this@visit) }
            super.visitClass(klass)
        }

        override fun visitNamedFunction(function: KtNamedFunction) {
            processors.filter { it.shouldRunOn(function) }.forEach { it.process(function, this@visit) }
            super.visitNamedFunction(function)
        }

        override fun visitReferenceExpression(expression: KtReferenceExpression) {
            processors.filter { it.shouldRunOn(expression) }.forEach { it.process(expression, this@visit) }
            super.visitReferenceExpression(expression)
        }
    })
}
