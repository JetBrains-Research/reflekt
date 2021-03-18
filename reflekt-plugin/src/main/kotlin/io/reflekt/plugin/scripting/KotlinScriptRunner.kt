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

class KotlinScriptRunner(classpath: List<File>) {
    val scriptEngine = KotlinJsr223JvmLocalScriptEngine(
        KotlinJsr223JvmLocalScriptEngineFactory(),
        classpath.plus(classpathFromClass<KotlinStandardJsr223ScriptTemplate>()!!),
        KotlinStandardJsr223ScriptTemplate::class.qualifiedName!!,
        { ctx, types -> ScriptArgsWithTypes(arrayOf(ctx.getBindings(ScriptContext.ENGINE_SCOPE)), types ?: emptyArray()) },
        arrayOf(Bindings::class)
    )

    init {
        scriptEngine.setBindings(scriptEngine.createBindings(), ScriptContext.ENGINE_SCOPE)
    }

    fun addImports(imports: List<Import>) {
        scriptEngine.eval(imports.joinToString(separator = "\n") { it.text })
    }

    inline fun <reified T> addValue(name: String, value: T): Unit = with(scriptEngine) {
        put(name, value)
        eval("""val $name = bindings["$name"] as ${T::class.qualifiedName}""")
    }

    fun <T> addValue(name: String, value: T, type: ParameterizedType): Unit = with(scriptEngine) {
        put(name, value)
        eval("""val $name = bindings["$name"] as ${type.render()}""")
    }

    fun eval(code: String): Any = scriptEngine.eval(code)
}
