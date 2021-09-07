package io.reflekt

import kotlin.Any
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Set
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

object ReflektImpl {
    fun objects() = Objects()

    fun classes() = Classes()

    fun functions() = Functions()

    class Objects {
        fun <T> withSupertypes(fqNames: Set<String>) = WithSupertypes<T>(fqNames)

        fun <T> withAnnotations(annotationFqNames: Set<String>, supertypeFqNames: Set<String>) =
                WithAnnotations<T>(annotationFqNames, supertypeFqNames)

        class WithSupertypes<T>(
            val fqNames: Set<String>
        ) {
            fun toList(): List<T> = emptyList()

            fun toSet(): Set<T> = toList().toSet()
        }

        class WithAnnotations<T>(
            val annotationFqNames: Set<String>,
            val supertypeFqNames: Set<String>
        ) {
            fun toList(): List<T> = when (annotationFqNames) {
                else -> emptyList()
            }

            fun toSet(): Set<T> = toList().toSet()
        }
    }

    class Classes {
        fun <T : Any> withSupertypes(fqNames: Set<String>) = WithSupertypes<T>(fqNames)

        fun <T : Any> withAnnotations(annotationFqNames: Set<String>, supertypeFqNames: Set<String>)
                = WithAnnotations<T>(annotationFqNames, supertypeFqNames)

        class WithSupertypes<T : Any>(
            val fqNames: Set<String>
        ) {
            fun toList(): List<KClass<T>> = when (fqNames) {
                setOf("io.reflekt.codegen.test.BInterfaceTest") ->
                        listOf(io.reflekt.codegen.test.B1::class as KClass<T>,
                        io.reflekt.codegen.test.B2::class as KClass<T>,
                        io.reflekt.codegen.test.B3::class as KClass<T>,
                        io.reflekt.codegen.test.B3.B4::class as KClass<T>)
                else -> emptyList()
            }

            fun toSet(): Set<KClass<T>> = toList().toSet()
        }

        class WithAnnotations<T : Any>(
            val annotationFqNames: Set<String>,
            val supertypeFqNames: Set<String>
        ) {
            fun toList(): List<KClass<T>> = when (annotationFqNames) {
                else -> emptyList()
            }

            fun toSet(): Set<KClass<T>> = toList().toSet()
        }
    }

    class Functions {
        fun <T : Any> withAnnotations(annotationFqNames: Set<String>) =
                WithAnnotations<T>(annotationFqNames)

        class WithAnnotations<T : Any>(
            val annotationFqNames: Set<String>
        ) {
            fun toList(): List<KFunction<T>> = emptyList()

            fun toSet(): Set<KFunction<T>> = toList().toSet()
        }
    }
}
