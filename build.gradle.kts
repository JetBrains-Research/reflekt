import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.cqfn.diktat.plugin.gradle.DiktatExtension
import org.cqfn.diktat.plugin.gradle.DiktatGradlePlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

plugins {
    `maven-publish`

    alias(libs.plugins.detekt)
    alias(libs.plugins.diktat)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.versions)
    id("org.jetbrains.reflekt.conventions")
}

group = "org.jetbrains.reflekt"
version = libs.versions.kotlin.get()
description = "Reflekt is a compile-time reflection library that leverages the flows of the standard reflection approach and can find classes, objects " +
        "(singleton classes) or functions by some conditions in compile-time."

allprojects {
    apply(plugin = "kotlin")
    apply(plugin = "maven-publish")

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
        dependsOn(
            tasks.withType<PublishToMavenLocal>(),
            ":reflekt-plugin:jar",
            gradle.includedBuild("using-embedded-kotlin").task(":reflekt-dsl:jar")
        )
    }

    apply<DiktatGradlePlugin>()
    configure<DiktatExtension> {
        diktatConfigFile = rootProject.file("diktat-analysis.yml")

        inputs {
            include("src/main/**/*.kt")
        }
    }

    apply<DetektPlugin>()

    configure<DetektExtension> {
        config.setFrom(rootProject.files("detekt.yml"))
        buildUponDefaultConfig = true
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
    diktatConfigFile = rootProject.file("diktat-analysis.yml")
    inputs {
        include("./*.kts")
    }
}

tasks.register("diktatCheckAll") {
    group = LifecycleBasePlugin.VERIFICATION_GROUP

    allprojects {
        this@register.dependsOn(tasks.getByName("diktatCheck"))
    }

    this@register.dependsOn(gradle.includedBuild("using-embedded-kotlin").task(":diktatCheckAll"))
}

tasks.register("diktatFixAll") {
    group = LifecycleBasePlugin.VERIFICATION_GROUP

    allprojects {
        this@register.dependsOn(tasks.getByName("diktatFix"))
    }
    this@register.dependsOn(gradle.includedBuild("using-embedded-kotlin").task(":diktatFixAll"))
}

subprojects {
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

for (it in listOf("publishAllPublicationsToSpacePackagesRepository", "publishToMavenLocal")) {
    tasks[it].dependsOn(
        gradle.includedBuild("using-embedded-kotlin").task(":reflekt-core:$it"),
        gradle.includedBuild("using-embedded-kotlin").task(":reflekt-dsl:$it"),
        gradle.includedBuild("using-embedded-kotlin").task(":gradle-plugin:$it"),
    )
}
