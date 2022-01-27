package org.jetbrains.reflekt.plugin.util

import org.gradle.internal.impldep.org.apache.commons.lang.SystemUtils
import java.io.File
import java.net.URL
import java.nio.file.*


object MavenLocalUtil {
    // We should use a const here since we can not get it from the project
    private const val KOTLIN_VERSION = "1.5.31"
    private const val REFLEKT_VERSION = "$KOTLIN_VERSION-1"
    private val mavenLocalPath = getMavenLocalPath()

    fun getReflektProjectJars(): Set<File> {
        val baseReflektPath = "$mavenLocalPath/org/jetbrains/reflekt"
        val reflektNames = listOf("gradle-plugin", "reflekt-core", "reflekt-dsl")
        return reflektNames.map {
            File("$baseReflektPath/$it/$REFLEKT_VERSION/$it-$REFLEKT_VERSION.jar")
        }.toSet()
    }

    fun getStdLibJar(pathToDownload: File): File {
        val jarName = "kotlin-stdlib-$KOTLIN_VERSION.jar"
        val suffix = "org/jetbrains/kotlin/kotlin-stdlib/$KOTLIN_VERSION/$jarName"
        val fileToDownload = File(pathToDownload, jarName)
        val localJar = File("$mavenLocalPath/$suffix")

        return when {
            fileToDownload.exists() -> fileToDownload
            localJar.exists() -> localJar
            else -> {
                val remoteUrl = "https://repo1.maven.org/maven2/$suffix"
                val remoteUrlStream = URL(remoteUrl).openStream()
                Files.copy(remoteUrlStream, Paths.get(fileToDownload.absolutePath), StandardCopyOption.REPLACE_EXISTING)

                fileToDownload
            }
        }
    }

    private fun getMavenLocalPath(): String = "${getHomeFolder()}/.m2/repository"

    private fun getHomeFolder(windowsUserProfile: String = "USERPROFILE"): String {
        if (SystemUtils.IS_OS_WINDOWS) {
            return System.getenv(windowsUserProfile).removeSuffix("/")
        }
        return Util.runProcessBuilder(Util.Command(listOf("/bin/bash", "-c", "echo \$HOME"))).removeSuffix("/")
    }

}
