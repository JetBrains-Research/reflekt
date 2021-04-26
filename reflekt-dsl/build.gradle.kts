import tanvd.kosogor.proxy.publishJar

group = rootProject.group
version = rootProject.version

dependencies {
    api(kotlin("compiler-embeddable"))
}


publishJar {}
