package io.arrowkt.example

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.quotes.*
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.calls.callUtil.getType


const val ROUTES_OBJECT = "AllRoutes"

val Meta.replacing: CliPlugin
    get() =
        "Replace assignments" {
            meta(
                // Todo: check selectorExpression too
                dotQualifiedExpression(this, { receiverExpression.text == ROUTES_OBJECT }) { c ->
                    Transform.replace(
                        replacing = c,
                        newDeclaration = replaceIntoFunctions(functionsNamesWithAnnotations).block
                    )
                }
            )
        }

fun replaceIntoFunctions(functionsNames: List<KtNamedFunction>): String {
    return """listOf(${functionsNames.joinToString(separator = ",") { "{ ${it.name}() }" }})"""
}
