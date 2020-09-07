package io.arrowkt.example

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.phases.analysis.body
import arrow.meta.phases.analysis.bodySourceAsExpression
import arrow.meta.quotes.Transform
import arrow.meta.quotes.namedFunction
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

const val ROUTE_ANNOTATION = "Route"

val functionsNamesWithAnnotations = mutableListOf<KtNamedFunction>()

val Meta.annotation: CliPlugin
    get() =
        "Annotation" {
            meta(
                    namedFunction(this, { validateFunction() }) { c ->
                        functionsNamesWithAnnotations.add(c)
                        Transform.empty
                    }
            )
        }


private fun KtNamedFunction.validateFunction(): Boolean =
        hasAnnotation(ROUTE_ANNOTATION)

fun KtAnnotated.hasAnnotation(
        vararg annotationNames: String
): Boolean {
    val names = annotationNames.toHashSet()
    val predicate: (KtAnnotationEntry) -> Boolean = {
        it.typeReference
                ?.typeElement
                ?.safeAs<KtUserType>()
                ?.referencedName in names
    }
    return annotationEntries.any(predicate)
}
