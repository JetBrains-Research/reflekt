[![JetBrains Research](https://jb.gg/badges/research.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
![Gradle Build](https://github.com/nbirillo/reflekt/workflows/Gradle%20Build/badge.svg?branch=master)

# Reflekt

Reflekt is a compile-time reflection library that leverages the flows of the 
standard reflection approach and can find classes, objects (singleton classes), or functions 
by some conditions in compile-time.

Instead of relying on JVM reflection, Reflekt performs compile-time resolution of reflection queries
using Kotlin compiler analysis, providing a convenient reflection API without actually using
reflection.

Reflekt is a joint project of [JetBrains Research](https://research.jetbrains.org/) and
the [Kotless](https://github.com/JetBrains/kotless) team. The main reason for its creation was the
necessity of GraalVM support in modern Java applications, especially on Serverless workloads. With
the help of the Reflekt project, Kotless will be able to provide access to GraalVM to users of
historically reflection-based frameworks such as Spring or their own Kotless DSL.

We have implemented two approaches - searching classes/objects or functions via a limited DSL 
and by custom user condition via an extended DSL. 
The first one will be called `Reflekt`, and the second `SmartReflekt`.

**Restrictions**. Reflekt analyses only `.kt` files (in the project and in the libraries); uses
Kotlin `1.5.30`. Reflekt does not currently support incremental compilation.

**Note**, we use [Intermediate Representation](https://kotlinlang.org/docs/whatsnew14.html#unified-backends-and-extensibility) of code in this plugin.
It means, that Reflekt can be used for all available platforms: JVM, Native and JavaScript.
___

## Table of contents

- [Getting started](#getting-started)
- [Local start](#local-start)
- [Supported features](#supported-features)
- [Examples](#examples)
- [Want to know more?](#want-to-know-more?)

## Getting started

**Note**, currently we support the following Reflekt and Kotlin versions:
`1.5.30`, `1.5.21`, `1.5.20`, `1.5.10`, `1.5.0`

Reflekt uses Gradle. If you have a Gradle project, you need to do the following steps to set up the Reflekt plugin.

Firstly, apply the plugin. In the `build.gradle.kts` file,
add the following lines in the `plugins` section:

```kotlin
plugins {
    // Version of Kotlin should be 1.5.0+ that supports IR backend
    kotlin("jvm") version "1.5.30" apply true

    // Please, use the same version with the Kotlin version in your project
    id("org.jetbrains.reflekt") version "1.5.30" apply true

    // Necessary only for this example, for Kotless library
    id("io.kotless") version "0.1.6" apply true
}
```

At the same time, add to the `settings.gradle.kts` file the following snippet:

```kotlin
pluginManagement {
    resolutionStrategy {
        this.eachPlugin {
            
            if (requested.id.id == "org.jetbrains.reflekt") {
                useModule("org.jetbrains.reflekt:gradle-plugin:${this.requested.version}")
            }
        }
    }

    repositories {
        //add the dependency to Reflekt Maven repository
        maven(url = uri("https://packages.jetbrains.team/maven/p/reflekt/reflekt"))

        // Necessary only for this example, for Kotless library
        maven(url = uri("https://plugins.gradle.org/m2/"))
    }
}
```
And add the following lines in the `repositories` section of `build.gradle.kts` file:

```kotlin
repositories {
    //... Any other repositories
    // add Reflekt repository for libraries resolving
    maven(url = uri("https://packages.jetbrains.team/maven/p/reflekt/reflekt"))
}
```

Next, customize the Reflekt plugin. In the `build.gradle.kts` file, add the `reflekt` block:

```kotlin
reflekt {
    // Enable or disable Reflekt plugin
    enabled = true
    // List of external libraries for dependencies search
    // Use only DependencyHandlers which have canBeResolve = True
    // Note: Reflekt works only with kt files from libraries
    librariesToIntrospect = listOf("io.kotless:kotless-dsl:0.1.6")
}
```

_Please note that the `librariesToIntrospect` argument should contain only the dependencies that you
use in the `dependencies` section. These dependencies may be implemented in Java or Kotlin language,
but the analysis will be made only on Kotlin files._


All of the above gives you access to [the limited  Reflekt DSL](./reflekt-dsl/src/main/kotlin/org/jetbrains/reflekt/Reflekt.kt) and [the extended SmartReflekt DSL](./reflekt-dsl/src/main/kotlin/org/jetbrains/reflekt/SmartReflekt.kt).

Now you can use the Reflekt plugin to find objects, classes, and functions in your project:

```kotlin
val objects = Reflekt.objects().withSupertype<AInterface>()
    .withAnnotations<AInterface>(FirstAnnotation::class, SecondAnnotation::class).toList()

val classes = Reflekt.classes().withSupertype<BInterface>().toSet()

val functions = Reflekt.functions().withAnnotations<() -> Unit>().toList()
```

And the SmartReflekt plugin to find the same instances but by custom condition:

```kotlin
val objects = SmartReflekt.objects<AInterface>().filter { TODO("some user's condition") }.resolve()

val classes = SmartReflekt.classes<BInterface>().filter { TODO("some user's condition") }.resolve()

val functions =
    SmartReflekt.functions<() -> Unit>().filter { TODO("some user's condition") }.toList()
```

## Local start

You can use `any` unpublished Reflekt version. You should do the following steps:

- Clone the Reflekt project (the official repo, any fork, branch, etc.).
- Build the project `./gradlew build`
- Publish the project to maven local `./gradlew publishToMavenLocal`
- Add `mavenLocal()` in the repositories section in the `build.gradle.kts` file in your project:

```kotlin
repositories {
    mavenLocal()
}
```

Please note that if you build a Reflekt version with a customized
[version](https://github.com/JetBrains-Research/reflekt/blob/master/build.gradle.kts#L4) number,
write this version in the plugins and dependencies sections.

## Supported features

- [x] Compile-time reflection by [Reflekt DSL](./reflekt-dsl/src/main/kotlin/org/jetbrains/reflekt/Reflekt.kt)
  for `multi-module` projects:
    - [x] project's files
    - [x] external libraries
- [x] Compile-time reflection by custom users' filters for `multi-module` projects
  by [SmartReflekt DSL](./reflekt-dsl/src/main/kotlin/org/jetbrains/reflekt/SmartReflekt.kt)
    - [x] project's files
    - [ ] external libraries
- [x] Bytecode generation -> IR generation
- [ ] Incremental compilation process
- [ ] Search in all modules of the project
- [ ] Code generation.

_Note: We analyze modules independently of each other. If an object\class\function is in module A,
and you run Reflekt in module B, then the object\class\function will not be found. You can find this
example in the [examples folder](./examples)._

## Examples

Any explanation becomes much better with a proper example.

In the repository's [examples folder](./examples), you can find an example project that uses the
Reflekt plugin by [Reflekt DSL](./reflekt-dsl/src/main/kotlin/org/jetbrains/reflekt/Reflekt.kt)
and by [SmartReflekt DSL](./reflekt-dsl/src/main/kotlin/org/jetbrains/reflekt/SmartReflekt.kt).

You can also find many examples of searching algorithm work in the [test](./reflekt-plugin/src/test)
folder.

By default the examples project uses Reflekt from the local maven repository. 
If you would like to use a released version, please, 
uncomment the corresponding lines in the `setting.gradle.kts` and `build.gradle.kts` files in the examples project.

## Want to know more?

The Reflekt code itself is widely documented, and you can take a look into its interfaces to get to
know Reflekt better.

You may ask questions and participate in discussions on
repository [issues](https://github.com/JetBrains-Research/reflekt/issues).
