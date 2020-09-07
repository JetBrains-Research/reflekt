package io.arrowkt.example

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.phases.analysis.body
import arrow.meta.phases.analysis.bodySourceAsExpression
import arrow.meta.quotes.Transform
import arrow.meta.quotes.namedFunction
import org.jetbrains.kotlin.psi.KtNamedFunction

val Meta.runAnnotatedFunctions: CliPlugin
    get() =
        "Running annotated functions" {
            meta(
                    namedFunction(this, { name == "main" }) { c ->
                        Transform.replace(
                                replacing = c,
                                newDeclaration = replace(c).function
                        )
                    }
            )
        }

fun replace(function: KtNamedFunction): String {
    val functionName = function.name
    val functionBody = function.body()?.bodySourceAsExpression()

    return """
|fun ${functionName}() {
              |   ${functionsNamesWithAnnotations[0]}()
              |   println("test message")
              | }"""
}