package org.jetbrains.reflekt.plugin.analysis

import org.jetbrains.reflekt.plugin.util.MavenLocalUtil.getReflektProjectJars
import org.jetbrains.reflekt.plugin.util.MavenLocalUtil.getStdLibJar
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class AnalysisSetupTest {

    @Test
    @Tag("analysis")
    fun `analysis setup test`() {
        // Just check if the all necessary ReflektJars exist
        assertDoesNotThrow { getReflektProjectJars() }
        assertDoesNotThrow { getStdLibJar() }
    }

    @Tag("analysis")
    @ParameterizedTest(name = "[{index}] expect jars contains {0}")
    @ValueSource(
        strings = [
            "gradle-plugin",
            "reflekt-core",
            "reflekt-dsl",
        ]
    )
    fun `expect reflekt project jars includes all projects`(reflektJarNamePrefix: String) {
        val jarNames = getReflektProjectJars().map { it.name }
        assertEquals(
            1,
            jarNames.count { it.startsWith(reflektJarNamePrefix) },
            "Expect jars $jarNames has one starting with $reflektJarNamePrefix"
        )
    }

    @Tag("analysis")
    @Test
    fun `expect kotlin-stdlib is available`() {
        val expectedPrefix = "kotlin-stdlib"
        assertTrue(
            getStdLibJar().name.startsWith(expectedPrefix),
            "expected stdlib jar to start with $expectedPrefix "
        )
    }
}
