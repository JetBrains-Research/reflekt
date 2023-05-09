plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
    // Uncomment it for using the last kotlin compiler version
    // The full list of the build can be found here:
    // https://teamcity.jetbrains.com/buildConfiguration/Kotlin_KotlinPublic_BuildNumber?mode=builds&tag=bootstrap
    // (see builds with <boostrap> tag)
    // Note: uncomment it also in the settings.gradle.kts
    // maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
}

dependencies {
    implementation(libs.gradle.plugin.kotlin)
    implementation(libs.gradle.plugin.diktat)
    implementation(libs.gradle.plugin.detekt)
}

java {
    targetCompatibility = JavaVersion.VERSION_11
    sourceCompatibility = JavaVersion.VERSION_11
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
