package io.reflekt.plugin.utils

import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.HasConvention
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.incremental.isKotlinFile
import java.io.File

internal inline fun <reified T : Any> Project.myExtByName(name: String): T = extensions.getByName(name) as T

internal val Project.mySourceSets: SourceSetContainer
    get() = myExtByName("sourceSets")

val SourceSet.kotlin: SourceDirectorySet
    get() =
        (this as HasConvention)
            .convention
            .getPlugin(KotlinSourceSet::class.java)
            .kotlin

internal val Project.myKtSourceSet: Set<File>
    get() = mySourceSets.asMap["main"]!!.allSource.files.filter { it.isKotlinFile(sourceFilesExtensions = listOf("kt")) }.toSet()
