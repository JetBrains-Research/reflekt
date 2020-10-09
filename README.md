# Reflekt

Reflekt is a compile-time reflection library that leverages flows of standard reflection approach.

Instead of relying on JVM reflection, Reflekt would perform compile-time resolution of reflection
queries using Kotlin compiler analysis. While in general, this approach is not always applicable for
most of the cases, Reflekt is capable of providing a convenient reflection API without actually
using reflection.

Reflekt is a joint project of JetBrains Research and the Kotless team. The main reason for its
creation was the necessity of GraalVM support in modern Java applications.


## Getting started

Just clone the repo by `git clone https://github.com/nbirillo/reflekt.git`, 
push the plugin into the local maven repository by `./gradlew publishToMavenLocal`, and finally 
add the dependency `implementation("io.reflekt", "io.reflekt.gradle.plugin", "0.1.0")` 
into `build.gradle.kts` file in the `examples` folder.
