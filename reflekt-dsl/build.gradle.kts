@file:Suppress("EMPTY_BLOCK_STRUCTURE_ERROR")

import tanvd.kosogor.proxy.publishJar

group = rootProject.group
version = rootProject.version

dependencies {
    api(kotlin("compiler-embeddable"))
}

publishJar {}
