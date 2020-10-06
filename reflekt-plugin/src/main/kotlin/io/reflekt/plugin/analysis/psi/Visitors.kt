package io.reflekt.plugin.analysis.psi

import io.reflekt.plugin.analysis.processor.Processor
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext


// TODO: rename??
fun KtElement.visit(processor: Processor<*>) {
    acceptChildren(object : KtDefaultVisitor() {
        override fun visitObjectDeclaration(declaration: KtObjectDeclaration) {
            if (processor.shouldRunOn(declaration)) processor.process(declaration)
            super.visitObjectDeclaration(declaration)
        }

        override fun visitClass(klass: KtClass) {
            if (processor.shouldRunOn(klass)) processor.process(klass)
            super.visitClass(klass)
        }

        override fun visitReferenceExpression(expression: KtReferenceExpression) {
            if (processor.shouldRunOn(expression)) processor.process(expression)
            super.visitReferenceExpression(expression)
        }
    })
}
