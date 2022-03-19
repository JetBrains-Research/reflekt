package org.jetbrains.reflekt.buildutils

plugins {
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

plugins.withType(JavaPlugin::class) {
    publishing.publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            reflektPom()
        }
    }
}

fun MavenPublication.reflektPom() = pom {
    description.set(ProjectMetadata.description)
    url.set(ProjectMetadata.homepage)
    licenses {
        license {
            name.set(ProjectMetadata.licenseName)
            url.set(ProjectMetadata.licenseUrl)
        }
    }
    scm {
        connection.set(ProjectMetadata.scmConnection)
        url.set(ProjectMetadata.scmHomepage)
    }
}
