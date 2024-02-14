import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.cqfn.diktat.plugin.gradle.DiktatExtension
import org.cqfn.diktat.plugin.gradle.DiktatGradlePlugin
import org.jetbrains.dokka.gradle.DokkaTaskPartial
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import java.net.URL

plugins {
    `maven-publish`
    alias(libs.plugins.detekt)
    alias(libs.plugins.diktat)
    alias(libs.plugins.dokka)
    `embedded-kotlin`
}

group = "org.jetbrains.reflekt"
version = libs.versions.kotlin.get()
description = "Reflekt is a compile-time reflection library that leverages the flows of the standard reflection approach and can find classes, objects " +
        "(singleton classes) or functions by some conditions in compile-time."

allprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "kotlin")
    apply<DiktatGradlePlugin>()

    tasks.withType<KotlinCompile<*>> {
        kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
    }

    java {
        targetCompatibility = JavaVersion.VERSION_11
        sourceCompatibility = JavaVersion.VERSION_11
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
        kotlinOptions {
            jvmTarget = "11"
        }
    }

    repositories {
        mavenCentral()
    }

    // We should publish the project in the local maven repository before the tests running
    tasks.withType<Test> {
        dependsOn(tasks.withType<PublishToMavenLocal>(), ":reflekt-plugin:jar", ":reflekt-dsl:jar")
    }

    configure<DiktatExtension> {
        diktatConfigFile = rootProject.file("../diktat-analysis.yml")

        inputs {
            include("src/main/**/*.kt")
        }
    }

    apply<DetektPlugin>()

    configure<DetektExtension> {
        config.setFrom(rootProject.files("../detekt.yml"))
        buildUponDefaultConfig = true
        debug = true
    }

    publishing.repositories.maven("https://packages.jetbrains.team/maven/p/reflekt/reflekt") {
        name = "SpacePackages"

        credentials {
            username = System.getenv("JB_SPACE_CLIENT_ID").orEmpty()
            password = System.getenv("JB_SPACE_CLIENT_SECRET").orEmpty()
        }
    }
}

configure<DiktatExtension> {
    diktatConfigFile = rootProject.file("../diktat-analysis.yml")
    inputs {
        include("./*.kts")
    }
}

tasks.register("diktatCheckAll") {
    allprojects {
        this@register.dependsOn(tasks.getByName("diktatCheck"))
    }
}
tasks.register("diktatFixAll") {
    allprojects {
        this@register.dependsOn(tasks.getByName("diktatFix"))
    }
}

subprojects {
    if (this@subprojects.name != "reflekt-plugin") {
        apply(plugin = "org.jetbrains.dokka")

        tasks.withType<DokkaTaskPartial> {
            dokkaSourceSets.configureEach {
                sourceLink {
                    localDirectory = this@subprojects.file("src/main/kotlin")

                    remoteUrl =
                        uri("https://github.com/JetBrains-Research/reflekt/tree/master/using-embedded-kotlin/${this@subprojects.name}/src/main/kotlin/").toURL()
                }
            }
        }
    }

    if (this@subprojects.name != "gradle-plugin") {
        publishing.publications.create<MavenPublication>("mavenJava") {
            from(this@subprojects.components["java"])

            pom {
                description = rootProject.description
                inceptionYear = "2020"
                url = "https://github.com/JetBrains-Research/reflekt"

                licenses {
                    license {
                        comments = "Open-source license"
                        distribution = "repo"
                        name = "Apache License"
                        url = "https://github.com/JetBrains-Research/reflekt/blob/master/LICENSE"
                    }
                }

                scm {
                    connection = "scm:git:git@github.com:JetBrains-Research/reflekt.git"
                    developerConnection = "scm:git:git@github.com:JetBrains-Research/reflekt.git"
                    url = "git@github.com:JetBrains-Research/reflekt.git"
                }
            }
        }
    }
}
