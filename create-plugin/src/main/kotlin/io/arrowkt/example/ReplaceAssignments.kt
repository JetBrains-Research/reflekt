package io.arrowkt.example

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.quotes.*
import org.jetbrains.kotlin.psi.KtNamedFunction

val Meta.replacing: CliPlugin
    get() =
        "Replace assignments" {
            meta(
                dotQualifiedExpression(this, { true }) { c ->
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
