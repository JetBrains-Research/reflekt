package org.jetbrains.reflekt.buildutils

import org.gradle.kotlin.dsl.*

plugins {
    base
    `maven-publish`
}

// TODO change
// JB_SPACE_CLIENT_ID -> ORG_GRADLE_PROJECT_SpacePackagesUsername
// JB_SPACE_CLIENT_SECRET -> ORG_GRADLE_PROJECT_SpacePackagesPassword
// and remove 'credentials', gradle will auto-configure based on the maven repo name
// see https://docs.gradle.org/current/samples/sample_publishing_credentials.html
val jbSpaceClientId = providers.environmentVariable("JB_SPACE_CLIENT_ID").orElse("")
val jbSpaceClientSecret = providers.environmentVariable("JB_SPACE_CLIENT_SECRET").orElse("")

publishing {
    repositories {
        maven("https://packages.jetbrains.team/maven/p/reflekt/reflekt") {
            name = "SpacePackages"
            credentials {
                username = jbSpaceClientId.get()
                password = jbSpaceClientSecret.get()
            }
        }
    }
}

tasks.publishToMavenLocal {
    dependsOn(tasks.check)
}
