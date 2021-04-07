package io.reflekt.plugin.scripting

import io.reflekt.plugin.analysis.models.Import
import io.reflekt.plugin.analysis.models.ParameterizedType
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Tag
import kotlin.script.experimental.jvm.util.KotlinJars

@Tag("scripting")
class KotlinScriptRunnerTest {
    @Test
    fun simpleEval() {
        val runner = KotlinScriptRunner(listOf(KotlinJars.stdlib))
        assertEquals(42, runner.eval("30 + 12"))
    }

    @Test
    fun addValueWithPlainType() {
        val runner = KotlinScriptRunner(listOf(KotlinJars.stdlib))
        val x = "hello"
        runner.addValue("testName", x)
        assertEquals(x, runner.eval("testName"))
    }

    @Test
    fun addValueWithParameterizedType() {
        val runner = KotlinScriptRunner(listOf(KotlinJars.stdlib))
        val x = listOf("a", "b", "c")
        runner.addValue("testName", x, ParameterizedType("kotlin.collections.List", listOf(ParameterizedType("kotlin.String"))))
        assertEquals(x, runner.eval("testName"))
    }

    @Test
    fun addImports() {
        val runner = KotlinScriptRunner(listOf(KotlinJars.stdlib))
        val x = "hello"
        runner.addValue("testName", x)

        runner.addImports(listOf(Import("kotlin.reflect.KClass", "import kotlin.reflect.KClass")))
        runner.run("val clazz: KClass<*> = testName::class")
        assertEquals("kotlin.String", runner.eval("clazz.qualifiedName"))
    }
}
