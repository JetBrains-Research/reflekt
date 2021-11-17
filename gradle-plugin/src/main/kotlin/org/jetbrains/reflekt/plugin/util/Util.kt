package org.jetbrains.reflekt.plugin.util

import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.HasConvention
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

internal val Project.mySourceSets: SourceSetContainer
    get() = myExtByName("sourceSets")

val SourceSet.kotlin: SourceDirectorySet
    get() =
        (this as HasConvention)
            .convention
            .getPlugin(KotlinSourceSet::class.java)
            .kotlin

internal inline fun <reified T : Any> Project.myExtByName(name: String): T = extensions.getByName(name) as T
