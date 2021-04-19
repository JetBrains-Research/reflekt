package io.reflekt.plugin.scripting

import io.reflekt.plugin.analysis.AnalysisSetupTest
import io.reflekt.plugin.analysis.models.Import
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

@Tag("scripting")
class KotlinScriptTest {
    @Test
    fun simpleEval() {
        val script = KotlinScript("30 + 12")
        assertEquals(42, script.eval())
    }

    @Test
    fun scriptWithProperties() {
        val script = KotlinScript(
            properties = listOf("a" to Array::class, "b" to String::class),
            code = "a.size.toString() + b",
        )
        assertEquals("42",
            script.eval(listOf(arrayOf(1, 2, 3, 4), "2"))
        )
    }

    @Test
    fun scriptWithExtendedClasspath() {
        assertThrows<RuntimeException> {
            KotlinScript("import io.reflekt.Reflekt").run()
        }

        assertDoesNotThrow {
            KotlinScript(
                code = "import io.reflekt.Reflekt",
                classpath = AnalysisSetupTest.getReflektProjectJars().toList()
            ).run()
        }
    }

    @Test
    fun scriptWithImports() {
        assertEquals(
            "kotlin.String",
            KotlinScript(
                code = """
                    val clazz: KClass<*> = t::class
                    clazz.qualifiedName
                """.trimIndent(),
                imports = listOf(Import("kotlin.reflect.KClass", "import kotlin.reflect.KClass")),
                properties = listOf("t" to String::class)
            ).eval(listOf("hello"))
        )
    }
}
