package org.jetbrains.reflekt.plugin.util

import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer

internal val Project.sourceSets: SourceSetContainer
    get() = extensions.getByName("sourceSets") as SourceSetContainer
