package org.jetbrains.reflekt.plugin.compiler.providers

import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoot
import org.jetbrains.kotlin.test.services.TestServices
import org.jetbrains.kotlin.test.services.assertions
import java.io.File
import java.io.FilenameFilter

object ReflektClasspathProvider {
    const val REFLEKT_PLUGIN = "reflekt-plugin"
    const val REFLEKT_DSL = "reflekt-dsl"

    fun findJar(moduleName: String, testServices: TestServices): File {
        val libDir = File(jarDir(moduleName))
        testServices.assertions.assertTrue(libDir.exists() && libDir.isDirectory, failMessage(moduleName))
        return libDir.listFiles(jarFilter(moduleName))?.firstOrNull() ?: testServices.assertions.fail(failMessage(moduleName))
    }

    fun getAbsolutePathsOfDefaultJars(): List<File> {
        // TODO: delete it on Wednesday (29/06/22) after a fix to a new kotlin bootstrap version
        val paths = listOf(
            "/Users/Anastasiia.Birillo/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlin/kotlin-stdlib/1.7.20-dev-2312/3ebacef5a52b7aebde394ea8099a8ec830b469f5/kotlin-stdlib-1.7.20-dev-2312.jar",
            "/Users/Anastasiia.Birillo/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlin/kotlin-stdlib-jdk8/1.7.20-dev-2312/aa2356f73d1c071062969a5baef6c58959e0900e/kotlin-stdlib-jdk8-1.7.20-dev-2312.jar",
            "/Users/Anastasiia.Birillo/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlin/kotlin-reflect/1.7.20-dev-2312/c08d3bd557e22544a49748f5f40828adef2e7930/kotlin-reflect-1.7.20-dev-2312.jar",
            "/Users/Anastasiia.Birillo/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlin/kotlin-test/1.7.20-dev-2312/4dc95c8ab09be4e3770231cba11b56a08317ed52/kotlin-test-1.7.20-dev-2312.jar",
            "/Users/Anastasiia.Birillo/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlin/kotlin-script-runtime/1.7.20-dev-2312/3b9ef3a82b31055ef453ee3e0c47a7086f6fb474/kotlin-script-runtime-1.7.20-dev-2312.jar",
            "/Users/Anastasiia.Birillo/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlin/kotlin-annotations-jvm/1.7.20-dev-2312/5735f44b4581d8dfcffb193923b7c5b889f0aa7e/kotlin-annotations-jvm-1.7.20-dev-2312.jar"
        )
        return paths.map { File(it) }
    }

    private fun jarDir(moduleName: String) = "$moduleName/build/libs/"

    private fun jarFilter(moduleName: String) = FilenameFilter { _, name -> name.startsWith(moduleName) && name.endsWith(".jar") && !name.contains("-sources") }

    private fun failMessage(moduleName: String) = { "Jar with Reflekt for the module: $moduleName does not exist. Please run :$moduleName:jar" }
}
