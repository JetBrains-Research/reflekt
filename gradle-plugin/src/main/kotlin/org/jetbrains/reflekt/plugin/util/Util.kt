package org.jetbrains.reflekt.plugin.util

import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.get

internal val Project.sourceSets: SourceSetContainer
    get() = extensions.getByName("sourceSets") as SourceSetContainer

val SourceSet.kotlin: SourceDirectorySet
    get() = this.extensions["kotlin"] as SourceDirectorySet
