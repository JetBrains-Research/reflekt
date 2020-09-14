import tanvd.kosogor.accessors.jar
import tanvd.kosogor.proxy.publishJar

group = rootProject.group
version = rootProject.version

dependencies {
    api(kotlin("stdlib"))
    api(kotlin("reflect"))
}

jar("jar") {
    this.exclude("io/reflekt/ReflektImpl.class")
}

publishJar {}
