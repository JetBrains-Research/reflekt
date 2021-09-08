package io.reflekt

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
        public fun <T> withSupertypes(fqNames: Set<String>) = WithSupertypes<T>(fqNames)

        public fun <T> withAnnotations(annotationFqNames: Set<String>,
                supertypeFqNames: Set<String>) = WithAnnotations<T>(annotationFqNames,
                supertypeFqNames)

        public class WithSupertypes<T>(
            public val fqNames: Set<String>
        ) {
            public fun toList(): List<T> = emptyList()

            public fun toSet(): Set<T> = toList().toSet()
        }

        public class WithAnnotations<T>(
            public val annotationFqNames: Set<String>,
            public val supertypeFqNames: Set<String>
        ) {
            public fun toList(): List<T> = when (annotationFqNames) {
                else -> emptyList()
            }

            public fun toSet(): Set<T> = toList().toSet()
        }
    }

    public class Classes {
        public fun <T : Any> withSupertypes(fqNames: Set<String>) = WithSupertypes<T>(fqNames)

        public fun <T : Any> withAnnotations(annotationFqNames: Set<String>,
                supertypeFqNames: Set<String>) = WithAnnotations<T>(annotationFqNames,
                supertypeFqNames)

        public class WithSupertypes<T : Any>(
            public val fqNames: Set<String>
        ) {
            public fun toList(): List<KClass<T>> = when (fqNames) {
                setOf("io.reflekt.codegen.test.BInterfaceTest",
                        "io.reflekt.codegen.test.AInterfaceTest") ->
                        listOf(io.reflekt.codegen.test.B1::class as KClass<T>,
                        io.reflekt.codegen.test.B2::class as KClass<T>,
                        io.reflekt.codegen.test.B3::class as KClass<T>,
                        io.reflekt.codegen.test.B3.B4::class as KClass<T>)
                setOf("kotlin.Any") -> listOf(io.reflekt.codegen.test.TestFunctions::class as
                        KClass<T>, io.reflekt.codegen.test.FirstAnnotationTest::class as KClass<T>,
                        io.reflekt.codegen.test.SecondAnnotationTest::class as KClass<T>,
                        io.reflekt.codegen.test.B1::class as KClass<T>,
                        io.reflekt.codegen.test.B2::class as KClass<T>,
                        io.reflekt.codegen.test.B3::class as KClass<T>,
                        io.reflekt.codegen.test.B3.B4::class as KClass<T>)
                else -> emptyList()
            }

            public fun toSet(): Set<KClass<T>> = toList().toSet()
        }

        public class WithAnnotations<T : Any>(
            public val annotationFqNames: Set<String>,
            public val supertypeFqNames: Set<String>
        ) {
            public fun toList(): List<KClass<T>> = when (annotationFqNames) {
                setOf("io.reflekt.codegen.test.FirstAnnotationTest") -> {
                    when (supertypeFqNames) {
                        setOf("kotlin.Any") -> listOf(io.reflekt.codegen.test.B2::class as
                                KClass<T>, io.reflekt.codegen.test.B3::class as KClass<T>)
                        else -> emptyList()
                    }
                }
                setOf("io.reflekt.codegen.test.FirstAnnotationTest") -> {
                    when (supertypeFqNames) {
                        setOf("io.reflekt.codegen.test.BInterfaceTest",
                                "io.reflekt.codegen.test.AInterfaceTest") ->
                                listOf(io.reflekt.codegen.test.B2::class as KClass<T>,
                                io.reflekt.codegen.test.B3::class as KClass<T>)
                        else -> emptyList()
                    }
                }
                else -> emptyList()
            }

            public fun toSet(): Set<KClass<T>> = toList().toSet()
        }
    }

    public class Functions {
        public fun <T : Any> withAnnotations(annotationFqNames: Set<String>) =
                WithAnnotations<T>(annotationFqNames)

        public class WithAnnotations<T : Any>(
            public val annotationFqNames: Set<String>
        ) {
            public fun toList(): List<KFunction<T>> = when (annotationFqNames) {
                else -> emptyList()
            }

            public fun toSet(): Set<KFunction<T>> = toList().toSet()
        }
    }
}