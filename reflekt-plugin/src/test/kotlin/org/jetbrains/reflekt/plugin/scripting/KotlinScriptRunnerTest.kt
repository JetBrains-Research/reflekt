package org.jetbrains.reflekt.plugin.scripting

import org.jetbrains.reflekt.plugin.analysis.models.Import
import org.jetbrains.reflekt.plugin.util.ReflektClasspathProvider
import org.junit.jupiter.api.*

class KotlinScriptRunnerTest {
    @Test
    fun simpleEval() {
        val script = KotlinScriptRunner("30 + 12")
        Assertions.assertEquals(42, script.eval())
    }

    @Test
    fun scriptWithProperties() {
        val script = KotlinScriptRunner(
            properties = listOf("a" to IntArray::class, "b" to String::class),
            code = "a.size.toString() + b",
        )
        Assertions.assertEquals(
            "42",
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
                classpath = listOf(ReflektClasspathProvider.REFLEKT_PLUGIN, ReflektClasspathProvider.REFLEKT_DSL)
                    .map {
                        ReflektClasspathProvider.findJar(it)
                    },
            ).run()
        }
    }

    @Test
    fun scriptWithImports() {
        Assertions.assertEquals(
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
}
