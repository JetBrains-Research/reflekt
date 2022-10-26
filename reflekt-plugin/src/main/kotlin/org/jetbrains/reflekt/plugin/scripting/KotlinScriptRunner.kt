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

/**
 * Allows running the KotlinScript interpreter at compile time.
 *
 * @param code the kotlin code that should be run (e.g. for lambda functions it is the lambda's body)
 * @param imports the list of imports that should be added into the KotlinScript classpath
 * @param properties the list of properties that should be used by KotlinScript:
 *  each variable from the [code] matched with its [KClass],
 *  e.g. for code <a.size.toString() + b> properties can be listOf("a" to Array::class, "b" to String::class)
 *  It means the KotlinScript expects Array for the <a> variable and String for the <b> variable
 * @param classpath the list of files that should be added into the KotlinScript classpath
 *
 * @property argumentNames the list of names from the properties list
 * @property source the full Kotlin source file that should be run (imports + code fragment)
 * @property compilationConfiguration configuration that works with extended classpath and KotlinScript properties
 */
@Suppress("KDOC_NO_CLASS_BODY_PROPERTIES_IN_HEADER", "KDOC_EXTRA_PROPERTY")
class KotlinScriptRunner(
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

    /**
     * Executes [source] and parses the result.
     *
     * @param arguments list of arguments to execute [source], for each name from [argumentNames]
     * @return parsed result
     */
    fun eval(arguments: List<*> = emptyList<Any>()): Any? =
        (execute(arguments).value.returnValue as ResultValue.Value).value

    /**
     * Executes [source].
     *
     * @param arguments list of arguments to execute [source], for each name from [argumentNames]
     */
    fun run(arguments: List<*> = emptyList<Any>()) {
        execute(arguments)
    }

    /**
     * Executes [source].
     *
     * @param arguments list of arguments to execute [source], for each name from [argumentNames]
     * @return a raw result from the KotlinScript
     */
    private fun execute(arguments: List<*>): ResultWithDiagnostics.Success<EvaluationResult> {
        val evaluationConfiguration = ScriptEvaluationConfiguration {
            // Spread operator here and `toTypedArray` causes a full copy
            // of the `argumentNames` list twice, but the performance impact is not significant
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
