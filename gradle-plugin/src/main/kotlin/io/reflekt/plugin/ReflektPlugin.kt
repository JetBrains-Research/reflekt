package io.reflekt.plugin

import io.reflekt.util.FileUtil
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import java.io.File

class ReflektPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        /*
         * Users can configure this extension in their build.gradle like this:
         * reflekt {
         *   enabled = false
         *   // ... set other members on the ReflektGradleExtension class
         * }
         */
        println("Reflekt gradle plugin")
        target.extensions.create(
            "reflekt",
            ReflektGradleExtension::class.java
        )
    }



}
