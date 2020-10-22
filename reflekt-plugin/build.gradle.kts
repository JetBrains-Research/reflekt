group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("compiler-embeddable"))
    implementation(gradleKotlinDsl())
    implementation(kotlin("gradle-plugin-api"))
    compileOnly("com.google.auto.service", "auto-service", "1.0-rc4")
    compile(project(":reflekt-core"))
}

