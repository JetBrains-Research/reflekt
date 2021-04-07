package io.reflekt.plugin.scripting

import io.reflekt.plugin.analysis.models.Import
import io.reflekt.plugin.analysis.models.ParameterizedType
import org.jetbrains.kotlin.cli.common.repl.ScriptArgsWithTypes
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngineFactory
import org.jetbrains.kotlin.script.jsr223.KotlinStandardJsr223ScriptTemplate
import java.io.File
import javax.script.Bindings
import javax.script.ScriptContext
import kotlin.script.experimental.jvm.util.classpathFromClass
import kotlin.script.templates.standard.ScriptTemplateWithBindings

/*
 * Executes kotlin scripts.
 * Note that every next evaluation is performed in the context of all previous evaluations.
 */
class KotlinScriptRunner(
    classpath: List<File> // List of jar files containing all necessary definitions.
) {
    val scriptEngine = KotlinJsr223JvmLocalScriptEngine(
        KotlinJsr223JvmLocalScriptEngineFactory(),
        classpath.plus(classpathFromClass<KotlinStandardJsr223ScriptTemplate>()!!).plus(classpathFromClass<ScriptTemplateWithBindings>()!!),
        KotlinStandardJsr223ScriptTemplate::class.qualifiedName!!,
        { ctx, types -> ScriptArgsWithTypes(arrayOf(ctx.getBindings(ScriptContext.ENGINE_SCOPE)), types ?: emptyArray()) },
        arrayOf(Bindings::class)
    )

    init {
        scriptEngine.setBindings(scriptEngine.createBindings(), ScriptContext.ENGINE_SCOPE)
    }

    // Adds specified imports to the context.
    fun addImports(imports: List<Import>) {
        scriptEngine.eval(imports.joinToString(separator = System.lineSeparator()) { it.text })
    }

    // Adds value with specified name to the context.
    // E.q. addValue("x", 42) executes code that is equivalent to "val x: Int = 42"
    inline fun <reified T> addValue(name: String, value: T): Unit = with(scriptEngine) {
        put(name, value)
        eval("""val $name = bindings["$name"] as ${T::class.qualifiedName}""")
    }

    // Adds value with specified name and parameterized type to the context.
    // E.q.:
    // addValue("x", listOf("a", "b"), ParameterizedType("kotlin.collections.List", listOf(ParameterizedType("kotlin.String", emptyList())))
    // executes code that is equivalent to
    // "val x: List<String> = listOf("a", "b")"
    fun <T> addValue(name: String, value: T, type: ParameterizedType): Unit = with(scriptEngine) {
        put(name, value)
        eval("""val $name = bindings["$name"] as ${type.render()}""")
    }

    // Evaluates any code in the context of previous evaluations.
    inline fun <reified T: Any> eval(code: String): T = scriptEngine.eval(code) as T

    fun run(code: String) {
        scriptEngine.eval(code)
    }
}
