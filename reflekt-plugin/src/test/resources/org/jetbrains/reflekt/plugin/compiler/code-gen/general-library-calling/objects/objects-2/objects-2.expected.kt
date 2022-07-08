package org.jetbrains.reflekt

import kotlin.Any
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Set
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

public object ReflektImpl {
    public fun objects() = Objects()

    public fun classes() = Classes()

    public fun functions() = Functions()

    public class Objects {
        public fun <T> withSuperTypes(fqNames: Set<String>) = WithSuperTypes<T>(fqNames)

        public fun <T> withAnnotations(annotationFqNames: Set<String>,
                supertypeFqNames: Set<String>) = WithAnnotations<T>(annotationFqNames,
                supertypeFqNames)

        public class WithSuperTypes<T>(
            public val fqNames: Set<String>,
        ) {
            public fun toList(): List<T> = when (fqNames) {
                setOf("org.jetbrains.reflekt.test.common.A2") ->
                        listOf(org.jetbrains.reflekt.test.common.A2 as T)
                else -> emptyList()
            }

            public fun toSet(): Set<T> = toList().toSet()
        }

        public class WithAnnotations<T>(
            public val annotationFqNames: Set<String>,
            public val supertypeFqNames: Set<String>,
        ) {
            public fun toList(): List<T> = when (annotationFqNames) {
                else -> emptyList()
            }

            public fun toSet(): Set<T> = toList().toSet()
        }
    }

    public class Classes {
        public fun <T : Any> withSuperTypes(fqNames: Set<String>) = WithSuperTypes<T>(fqNames)

        public fun <T : Any> withAnnotations(annotationFqNames: Set<String>,
                supertypeFqNames: Set<String>) = WithAnnotations<T>(annotationFqNames,
                supertypeFqNames)

        public class WithSuperTypes<T : Any>(
            public val fqNames: Set<String>,
        ) {
            public fun toList(): List<KClass<T>> = emptyList()

            public fun toSet(): Set<KClass<T>> = toList().toSet()
        }

        public class WithAnnotations<T : Any>(
            public val annotationFqNames: Set<String>,
            public val supertypeFqNames: Set<String>,
        ) {
            public fun toList(): List<KClass<T>> = when (annotationFqNames) {
                else -> emptyList()
            }

            public fun toSet(): Set<KClass<T>> = toList().toSet()
        }
    }

    public class Functions {
        public fun <T : Any> withAnnotations(annotationFqNames: Set<String>, signature: String) =
                WithAnnotations<T>(annotationFqNames, signature)

        public class WithAnnotations<T : Any>(
            public val annotationFqNames: Set<String>,
            public val signature: String,
        ) {
            public fun toList(): List<KFunction<T>> = when (annotationFqNames) {
                else -> emptyList()
            }

            public fun toSet(): Set<KFunction<T>> = toList().toSet()
        }
    }
}