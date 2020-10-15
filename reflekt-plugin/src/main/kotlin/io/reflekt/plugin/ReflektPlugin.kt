package io.reflekt.plugin

import io.reflekt.plugin.dsl.reflekt
import io.reflekt.plugin.tasks.GenerateReflektResolver
import io.reflekt.plugin.utils.kotlin
import io.reflekt.plugin.utils.mySourceSets
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get
import java.io.File

class ReflektPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            afterEvaluate {
                target.mySourceSets.apply {
                    this["main"].kotlin.srcDir(reflekt.generationPathOrDefault(target))
                }
            }

            val generate = tasks.create("reflekt", GenerateReflektResolver::class.java)
            tasks.getByName("classes").dependsOn(generate)
        }
    }

    private fun getFilesToIntrospect(target: Project): Set<File> {
        val filesToIntrospect: MutableSet<File> = HashSet()
        target.configurations.forEach { configuration ->
            val filtered = configuration.dependencies
                .filter { "${it.group}:${it.name}:${it.version}" in reflekt.librariesToIntrospect }
            filesToIntrospect.addAll(configuration.files(*filtered.toTypedArray()))
        }
        return filesToIntrospect
    }
}
