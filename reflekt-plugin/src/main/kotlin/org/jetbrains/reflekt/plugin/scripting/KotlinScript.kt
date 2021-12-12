@file:Suppress("FILE_WILDCARD_IMPORTS")

package org.jetbrains.reflekt.plugin.scripting

import org.jetbrains.reflekt.plugin.analysis.models.Import
import java.io.File
import kotlin.reflect.KClass
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.updateClasspath
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost

typealias KotlinScriptProperties = List<Pair<String, KClass<*>>>

class KotlinScript(
    code: String,
    imports: List<Import> = emptyList(),
    properties: KotlinScriptProperties = emptyList(),
    classpath: List<File> = emptyList(),
) {
    private val argumentNames = properties.map { it.first }
    private val source = (imports.joinToString(separator = System.lineSeparator(), postfix = System.lineSeparator()) { it.text } + code).toScriptSource()
    private val compilationConfiguration = ScriptCompilationConfiguration {
        jvm {
            updateClasspath(classpath)
        }
        // Spread operator here and `toTypedArray` causes a full copy of the `properties` list twice, but the performance impact is not significant
        @Suppress("SpreadOperator")
        providedProperties(*properties.toTypedArray())
    }

    fun eval(arguments: List<*> = emptyList<Any>()): Any? =
        (execute(arguments).value.returnValue as ResultValue.Value).value

    fun run(arguments: List<*> = emptyList<Any>()) {
        execute(arguments)
    }

    private fun execute(arguments: List<*>): ResultWithDiagnostics.Success<EvaluationResult> {
        val evaluationConfiguration = ScriptEvaluationConfiguration {
            // Spread operator here and `toTypedArray` causes a full copy of the `argumentNames` list twice, but the performance impact is not significant
            @Suppress("SpreadOperator")
            providedProperties(*argumentNames.zip(arguments).toTypedArray())
        }
        val result = BasicJvmScriptingHost().eval(
            source, compilationConfiguration, evaluationConfiguration,
        )
        if (result !is ResultWithDiagnostics.Success) {
            throw ReflektKotlinScriptException(result.reports.map { it.render(withStackTrace = true) })
        }
        return result
    }
}

private class ReflektKotlinScriptException(msg: List<String>) : RuntimeException("Failed to evaluate script:\n$msg")
