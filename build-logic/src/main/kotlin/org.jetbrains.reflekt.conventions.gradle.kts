@file:Suppress("DSL_SCOPE_VIOLATION")

import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val libs = the<LibrariesForLibs>()

plugins {
  id("org.jetbrains.kotlin.jvm")
}

val ktTarget = KotlinVersion.KOTLIN_2_0

val ktCompilerArgs = listOf(
    "-Xskip-prerelease-check",
    "-Xsuppress-version-warnings",
    "-Xallow-unstable-dependencies",
    "-opt-in=org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI",
)

kotlin {
  compilerOptions {
      apiVersion = ktTarget
      languageVersion = ktTarget
      freeCompilerArgs = freeCompilerArgs.get().plus(ktCompilerArgs)
  }
}

afterEvaluate {
    tasks.withType(KotlinCompile::class).configureEach {
        kotlinOptions {
            apiVersion = ktTarget.version
            languageVersion = ktTarget.version
            freeCompilerArgs = freeCompilerArgs.plus(ktCompilerArgs)
        }
    }
}
