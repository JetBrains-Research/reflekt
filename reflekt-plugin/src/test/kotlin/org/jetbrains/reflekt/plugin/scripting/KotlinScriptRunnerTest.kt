package org.jetbrains.reflekt.plugin.scripting

import org.jetbrains.reflekt.plugin.analysis.models.Import
import org.jetbrains.reflekt.plugin.util.MavenLocalUtil.getReflektProjectJars
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals

@Tag("scripting")
class KotlinScriptRunnerTest {
    @Test
    fun simpleEval() {
        val script = KotlinScriptRunner("30 + 12")
        assertEquals(42, script.eval())
    }

    @Test
    fun scriptWithProperties() {
        val script = KotlinScriptRunner(
            properties = listOf("a" to IntArray::class, "b" to String::class),
            code = "a.size.toString() + b",
        )
        assertEquals("42",
            script.eval(listOf(intArrayOf(1, 2, 3, 4), "2")),
        )
    }

    @Test
    fun scriptWithExtendedClasspath() {
        val code = "import org.jetbrains.reflekt.Reflekt\nval a = Reflekt.objects()"
        assertThrows<RuntimeException> {
            KotlinScriptRunner(code).run()
        }

        assertDoesNotThrow {
            KotlinScriptRunner(
                code = code,
                classpath = getReflektProjectJars().toList(),
            ).run()
        }
    }

    @Test
    fun scriptWithImports() {
        assertEquals(
            "kotlin.String",
            KotlinScriptRunner(
                code = """
                    val clazz: KClass<*> = t::class
                    clazz.qualifiedName
                """.trimIndent(),
                imports = listOf(Import("kotlin.reflect.KClass", "import kotlin.reflect.KClass")),
                properties = listOf("t" to String::class),
            ).eval(listOf("hello")),
        )
    }

    companion object {
        // Equals private val with same name from org.jetbrains.kotlin.scripting.compiler.plugin.impl
        private const val SCRIPT_COMPILATION_DISABLE_PLUGINS_PROPERTY = "script.compilation.disable.plugins"
        @BeforeAll
        @JvmStatic
        fun disableCompilerTestingPlugin() {
            System.setProperty(SCRIPT_COMPILATION_DISABLE_PLUGINS_PROPERTY, "com.tschuchort.compiletesting.MainComponentRegistrar")
        }
    }
}
