package io.reflekt.plugin.analysis

import io.reflekt.plugin.util.Util.Command
import io.reflekt.plugin.util.Util.runProcessBuilder
import io.reflekt.util.Util
import org.gradle.internal.impldep.org.apache.commons.lang.SystemUtils
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.File

class AnalysisSetupTest {

    companion object {
        private const val WINDOWS_USER_PROFILE = "USERPROFILE"

        // We should use a const here since we can not get it from the project
        fun getReflektProjectJars(version: String = "1.5.30"): Set<File> {
            val baseReflektPath = "${getMavenLocalPath()}/io/reflekt"
            val reflektNames = listOf("gradle-plugin", "reflekt-core", "reflekt-dsl")
            return reflektNames.map {
                val jar = File("$baseReflektPath/$it/$version/$it-$version.jar")
                jar
            }.toSet()
        }

        private fun getMavenLocalPath(): String = "${getHomeFolder()}/.m2/repository"

        private fun getHomeFolder(): String {
            if (SystemUtils.IS_OS_WINDOWS) {
                return System.getenv(WINDOWS_USER_PROFILE).removeSuffix("/")
            }
            return runProcessBuilder(Command(listOf("/bin/bash", "-c", "echo \$HOME"))).removeSuffix("/")
        }
    }

    @Test
    @Tag("analysis")
    fun `analysis setup test`() {
        /*
         * Just check if the all necessary ReflektJars exist
         */
        assertDoesNotThrow { getReflektProjectJars() }
    }
}
