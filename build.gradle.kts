import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.report.ReportMergeTask
import org.jetbrains.reflekt.buildutils.*

plugins {
    `maven-publish`
    alias(libs.plugins.kosogor)
    alias(libs.plugins.buildconfig) apply false
    alias(libs.plugins.dokka)
    id(libs.plugins.kotlin.jvm.get().pluginId)
}

val detektReportMerge by tasks.registering(ReportMergeTask::class) {
    output.set(rootProject.buildDir.resolve("reports/detekt/merge.sarif"))
}

group = "org.jetbrains.reflekt"
version = libs.versions.kotlin.asProvider().get()

allprojects {
    apply {
        plugin("kotlin")

        tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinCompile<*>> {
            kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
        kotlinOptions {
            jvmTarget = "11"
        }
    }

    repositories {
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
        // Uncomment it for using the last kotlin compiler version
        // The full list of the build can be found here:
        // https://teamcity.jetbrains.com/buildConfiguration/Kotlin_KotlinPublic_BuildNumber?mode=builds&tag=bootstrap
        // (see builds with <boostrap> tag)
        // Note: uncomment it also in the settings.gradle.kts
        // maven {
        // url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
        // }
    }

    // We should publish the project in the local maven repository before the tests running
    tasks.withType<Test> {
        dependsOn(tasks.withType<PublishToMavenLocal> {}, ":reflekt-plugin:jar", ":reflekt-dsl:jar")
    }

    configureDiktat()
    apply<DetektPlugin>()

    configure<DetektExtension> {
        config = rootProject.files("detekt.yml")
        buildUponDefaultConfig = true
        debug = true
    }

    tasks.withType<Detekt> {
        finalizedBy(detektReportMerge)
        reports.sarif.required.set(true)
        detektReportMerge.get().input.from(sarifReportFile)
    }
}

createDiktatTask()

subprojects {
    apply(plugin = "maven-publish")

    publishing {
        repositories {
            maven {
                name = "SpacePackages"
                url = uri("https://packages.jetbrains.team/maven/p/reflekt/reflekt")
                credentials {
                    username = System.getenv("JB_SPACE_CLIENT_ID")?.takeIf { it.isNotBlank() } ?: ""
                    password = System.getenv("JB_SPACE_CLIENT_SECRET")?.takeIf { it.isNotBlank() } ?: ""
                }
            }
        }
    }
}
