package io.reflekt.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class ReflektPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        /*
         * Users can configure this extension in their build.gradle like this:
         * reflekt {
         *   enabled = false
         *   // ... set other members on the ReflektGradleExtension class
         * }
         */
        target.extensions.create(
            "reflekt",
            ReflektGradleExtension::class.java
        )
    }
}
