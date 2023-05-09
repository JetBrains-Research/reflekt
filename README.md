[![JetBrains Research](https://jb.gg/badges/research.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
![Gradle Build](https://github.com/nbirillo/reflekt/workflows/Gradle%20Build/badge.svg?branch=master)
[![Run deteKT](https://github.com/JetBrains-Research/reflekt/actions/workflows/detekt.yml/badge.svg)](https://github.com/JetBrains-Research/reflekt/actions/workflows/detekt.yml)
[![Run diKTat](https://github.com/JetBrains-Research/reflekt/actions/workflows/diktat.yml/badge.svg)](https://github.com/JetBrains-Research/reflekt/actions/workflows/diktat.yml)

# Reflekt

Reflekt is a compile-time reflection library that leverages the flows of the 
standard reflection approach and can find classes, objects (singleton classes) or functions 
by some conditions in compile-time.

Instead of relying on JVM reflection, Reflekt performs compile-time resolution of reflection queries
using Kotlin compiler analysis, providing a convenient reflection API without actually using
reflection.

Reflekt is a joint project of [JetBrains Research](https://research.jetbrains.org/) and
the [Kotless](https://github.com/JetBrains/kotless) team. The main reason for its creation was the
necessity of GraalVM support in modern Java applications, especially on Serverless workloads. With
the help of the Reflekt project, Kotless will be able to provide access to GraalVM to users of
historically reflection-based frameworks such as Spring or their own Kotless DSL.

We have implemented two approaches - searching classes\objects or functions via a limited DSL 
and by custom user condition via an extended DSL. 
The first one will be called `Reflekt`, and the second `SmartReflekt`.

**Restrictions**. Reflekt analyses only `.kt` files (in the project and in the libraries); uses
Kotlin `1.7.0`. Reflekt does not currently support incremental compilation.

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
`1.7.0` (only for local usage), `1.5.30`, `1.5.21`, `1.5.20`, `1.5.10`, `1.5.0`

Reflekt uses Gradle. If you have a Gradle project, you only need to do three things.

Firstly, set up the Reflekt plugin. You need to apply the plugin. In the `build.gradle.kts` file,
add the following lines in the `plugins` section:

```kotlin
plugins {
    // Version of Kotlin should be 1.5.0+ that supports IR backend
    kotlin("jvm") version "1.7.0"

    // Please, use the same version with the Kotlin version in your project
    id("org.jetbrains.reflekt") version "1.7.0"

    // Necessary only for this example, for Kotless library
    id("io.kotless") version "0.1.6"
}
```

At the same time, add to the `settings.gradle.kts` file the following snippet:

```kotlin
pluginManagement {
    repositories {
        //add the dependency to Reflekt Maven repository
        maven(url = uri("https://packages.jetbrains.team/maven/p/reflekt/reflekt"))

        // Necessary only for this example, for Kotless library
        maven(url = uri("https://plugins.gradle.org/m2/"))
    }
}
```

Secondly, add the Reflekt DSL as a library to your application. In the `build.gradle.kts` file, add
the following lines in the `dependencies` section:

```kotlin
dependencies {
    // The version here and the version in the plugins sections should be equal
    implementation("org.jetbrains.reflekt", "reflekt-dsl", "1.7.0")

    // Necessary for this example
    compileOnly("io.kotless", "kotless-lang", "0.1.6")
}
```

At the same time, add the following lines in the `repositories` section:
```kotlin
repositories {
    //... Any other repositories
    // add Reflekt repository for libraries resolving
    maven(url = uri("https://packages.jetbrains.team/maven/p/reflekt/reflekt"))
}
```

Thirdly, customize the Reflekt plugin. In the `build.gradle.kts` file, add the `reflekt` object:

```kotlin
reflekt {
    // Enable or disable Reflekt plugin
    enabled = true
}
```

**Please note** that the Reflekt can also analyze the files from the external libraries. 
Reflekt can handle only libraries from the `dependencies` section and 
DependencyHandlers of them should be `canBeResolve = True`, e.g.:
```kotlin
val reflektConfiguration by configurations.creating {
    isCanBeResolved = true
}

configurations["implementation"].extendsFrom(reflektConfiguration)
```
Also, this library should contain a special file `ReflektMeta`. The last point means the library
should use Reflekt with the following configuration:
```kotlin
reflekt {
    // Enable or disable Reflekt plugin
    enabled = true
    // Create ReflektMeta file
    toSaveMetadata = true
}
```


To avoid some bugs and enable IR, please add the following compilation settings 
for Java and Kotlin in the `build.gradle.kts` file:

```kotlin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

kotlin.jvmToolchain(11)

tasks.withType<KotlinCompile> {
    kotlinOptions {
        // Current Reflekt version does not support incremental compilation process
        incremental = false
    }
}
```

**Note**: Please note that the current version of Reflekt and SmartReflekt does not support incremental
compilation process

This gives you access to [the limited  Reflekt DSL](./reflekt-dsl/src/main/kotlin/org/jetbrains/reflekt/Reflekt.kt)
interfaces.

This gives you access
to [the extended SmartReflekt DSL](./reflekt-dsl/src/main/kotlin/org/jetbrains/reflekt/SmartReflekt.kt), which allow
filtering classes/objects\functions by user condition.

Now you can use the Reflekt plugin to find objects, classes, and functions in your project:

```kotlin
val objects = Reflekt.objects().withSupertype<AInterface>()
    .withAnnotations<AInterface>(FirstAnnotation::class, SecondAnnotation::class).toList()

val classes = Reflekt.classes().withSupertype<BInterface>().toSet()

val functions = Reflekt.functions().withAnnotations<() -> Unit>().toList()
```

And the SmartReflekt plugin:

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

By default, the examples project uses Reflekt from the local maven repository. 
If you would like to use a released version, please, 
uncomment the corresponding lines in the `setting.gradle.kts` and `build.gradle.kts` files in the examples project.

## Want to know more?

The Reflekt code itself is widely documented, and you can take a look into its interfaces to get to
know Reflekt better.

You may ask questions and participate in discussions on
repository [issues](https://github.com/JetBrains-Research/reflekt/issues).

## Contribution

Please be sure to review project's [contributing guidelines](./docs/contributing.md) to learn how to help the project.
