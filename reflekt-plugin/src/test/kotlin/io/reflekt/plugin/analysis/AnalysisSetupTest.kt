package io.reflekt.plugin.analysis

import io.reflekt.plugin.util.Util.Command
import io.reflekt.plugin.util.Util.runProcessBuilder
import org.gradle.internal.impldep.org.apache.commons.lang.SystemUtils
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.File

class AnalysisSetupTest {

    companion object {
        // Todo: can we get the version from the project?
        fun getReflektJars(version: String = "0.1.0"): Set<File> {
            val baseReflektPath = "${getMavenLocalPath()}/io/reflekt"
            val reflektNames = listOf("gradle-plugin", "io.reflekt.core", "io.reflekt.dsl")
            return reflektNames.map {
                val jar = File("$baseReflektPath/$it/$version/$it-$version.jar")
                jar
            }.toSet()
        }

        // TODO: get it in Windows
        private fun getMavenLocalPath(): String {
            if (SystemUtils.IS_OS_WINDOWS) {
                error("Not supported yet")
            }
            return "${getHomeFolder()}/.m2/repository"
        }

        // TODO: get it in Windows
        private fun getHomeFolder(): String {
            if (SystemUtils.IS_OS_WINDOWS) {
                error("Not supported yet")
            }
            return runProcessBuilder(Command(listOf("/bin/bash", "-c", "echo \$HOME")))
        }
    }

    @Test
    @Tag("analysis")
    fun `analysis setup test`() {
        /*
         * Just check if the all necessary ReflektJars exist
         */
        assertDoesNotThrow { getReflektJars() }
    }
}
