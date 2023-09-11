import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.report.ReportMergeTask
import org.jetbrains.dokka.gradle.DokkaTaskPartial
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.reflekt.buildutils.*
import java.net.URL

@Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    `maven-publish`
    alias(libs.plugins.buildconfig) apply false
    alias(libs.plugins.dokka)
    id(libs.plugins.kotlin.jvm.get().pluginId)
}

val detektReportMerge by tasks.registering(ReportMergeTask::class) {
    output = rootProject.layout.buildDirectory.file("reports/detekt/merge.sarif")
}

group = "org.jetbrains.reflekt"
version = libs.versions.kotlin.get()
description = "Reflekt is a compile-time reflection library that leverages the flows of the standard reflection approach and can find classes, objects " +
        "(singleton classes) or functions by some conditions in compile-time."

allprojects {
    apply(plugin = "kotlin")

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
        // Uncomment it for using the last kotlin compiler version
        // The full list of the build can be found here:
        // https://teamcity.jetbrains.com/buildConfiguration/Kotlin_KotlinPublic_BuildNumber?mode=builds&tag=bootstrap
        // (see builds with <boostrap> tag)
        // Note: uncomment it also in the settings.gradle.kts
        // maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
    }

    // We should publish the project in the local maven repository before the tests running
    tasks.withType<Test> {
        dependsOn(tasks.withType<PublishToMavenLocal>(), ":reflekt-plugin:jar", ":reflekt-dsl:jar")
    }

    configureDiktat()
    apply<DetektPlugin>()

    configure<DetektExtension> {
        config.setFrom(rootProject.files("detekt.yml"))
        buildUponDefaultConfig = true
        debug = true
    }

    tasks.withType<Detekt> {
        finalizedBy(detektReportMerge)
        reports.sarif.required = true
        detektReportMerge.get().input.from(sarifReportFile)
    }
}

createDiktatTask()

subprojects {
    apply(plugin = "maven-publish")

    if (this@subprojects.name != "reflekt-plugin") {
        apply(plugin = "org.jetbrains.dokka")

        tasks.withType<DokkaTaskPartial> {
            dokkaSourceSets.configureEach {
                sourceLink {
                    localDirectory = this@subprojects.file("src/main/kotlin")

                    remoteUrl =
                        URL("https://github.com/JetBrains-Research/${rootProject.name}/tree/master/${this@subprojects.name}/src/main/kotlin/")
                }
            }
        }
    }

    if (this@subprojects.name != "gradle-plugin") {
        publishing {
            publications {
                @Suppress("unused")
                val mavenJava by creating(MavenPublication::class) {
                    from(this@subprojects.components["java"])

                    pom {
                        description = rootProject.description
                        inceptionYear = "2020"
                        url = "https://github.com/JetBrains-Research/${rootProject.name}"

                        licenses {
                            license {
                                comments = "Open-source license"
                                distribution = "repo"
                                name = "Apache License"
                                url = "https://github.com/JetBrains-Research/${rootProject.name}/blob/master/LICENSE"
                            }
                        }

                        scm {
                            connection = "scm:git:git@github.com:JetBrains-Research/${rootProject.name}.git"
                            developerConnection = "scm:git:git@github.com:JetBrains-Research/${rootProject.name}.git"
                            url = "git@github.com:JetBrains-Research/${rootProject.name}.git"
                        }
                    }
                }
            }
        }
    }

    publishing {
        repositories {
            maven("https://packages.jetbrains.team/maven/p/reflekt/reflekt") {
                name = "SpacePackages"

                credentials {
                    username = System.getenv("JB_SPACE_CLIENT_ID").orEmpty()
                    password = System.getenv("JB_SPACE_CLIENT_SECRET").orEmpty()
                }
            }
        }
    }
}
