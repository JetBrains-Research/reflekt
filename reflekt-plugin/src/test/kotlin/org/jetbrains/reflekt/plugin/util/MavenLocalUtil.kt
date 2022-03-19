package org.jetbrains.reflekt.plugin.util

import java.io.File

object MavenLocalUtil {

    /**
     * The directory that stores Reflekt jars.
     *
     * A task in `build.gradle.kts` fetches the jars built by other projects and stores them a directory.
     * The name of that directory is passed as a System Property, `reflektTestLibDir`, during test execution.
     */
    private val reflektTestLibDir: File = File(System.getProperty("reflektTestLibDir"))

    fun getReflektProjectJars(): Set<File> = getTestLibJars("gradle-plugin", "reflekt-core", "reflekt-dsl")

    fun getStdLibJar(): File = getTestLibJars("kotlin-stdlib").first()

    private fun getTestLibJars(vararg jarNames: String): Set<File> {
        val jars = reflektTestLibDir
            .walkTopDown()
            .filter { file ->
                file.extension == "jar" && jarNames.any { file.name.startsWith(it) }
            }
            .toSet()

        require(jars.size == jarNames.size) {
            "couldn't find all jars. Requested: ${jarNames.joinToString()}, actual: ${jars.joinToString { it.name }}"
        }

        return jars
    }
}
