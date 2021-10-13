import tanvd.kosogor.proxy.publishJar
import tanvd.kosogor.accessors.jar

group = rootProject.group
version = rootProject.version

dependencies {
    api(kotlin("compiler-embeddable"))
}

jar("jar") {
    this.exclude("io/reflekt/ReflektImpl*.class")
}

publishJar {}
