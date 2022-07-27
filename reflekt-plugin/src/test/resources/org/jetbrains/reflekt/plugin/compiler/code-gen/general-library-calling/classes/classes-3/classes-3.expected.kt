package org.jetbrains.reflekt

import kotlin.Any
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.Set
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

public object ReflektImpl {
    public val reflektClasses: Map<KClass<*>, ReflektClass<*>>

    init {
        val m = HashMap<KClass<*>, ReflektClassImpl<*>>()
        m[org.jetbrains.reflekt.test.common.BInterface::class] = ReflektClassImpl(kClass =
                org.jetbrains.reflekt.test.common.BInterface::class, annotations = hashSetOf(),
                isAbstract = true, isCompanion = false, isData = false, isFinal = false, isFun =
                false, isInner = false, isOpen = false, isSealed = false, isValue = false,
                qualifiedName = "org.jetbrains.reflekt.test.common.BInterface", simpleName =
                "BInterface", visibility = ReflektVisibility.PUBLIC, objectInstance = null)
        m[org.jetbrains.reflekt.test.common.B2::class] = ReflektClassImpl(kClass =
                org.jetbrains.reflekt.test.common.B2::class, annotations =
                hashSetOf(org.jetbrains.reflekt.test.common.FirstAnnotation(),
                org.jetbrains.reflekt.test.common.SecondAnnotation("Test",
                org.jetbrains.reflekt.test.common.FirstAnnotation(42, booleanArrayOf(false,
                true)))), isAbstract = false, isCompanion = false, isData = true, isFinal = true,
                isFun = false, isInner = false, isOpen = false, isSealed = false, isValue = false,
                qualifiedName = "org.jetbrains.reflekt.test.common.B2", simpleName = "B2",
                visibility = ReflektVisibility.PUBLIC, objectInstance = null)
        m[kotlin.Any::class] = ReflektClassImpl(kClass = kotlin.Any::class, annotations =
                hashSetOf(), isAbstract = false, isCompanion = false, isData = false, isFinal =
                false, isFun = false, isInner = false, isOpen = true, isSealed = false, isValue =
                false, qualifiedName = "kotlin.Any", simpleName = "Any", visibility =
                ReflektVisibility.PUBLIC, objectInstance = null)
        (m[org.jetbrains.reflekt.test.common.BInterface::class]!! as
                ReflektClassImpl<org.jetbrains.reflekt.test.common.BInterface>).superclasses +=
                m[kotlin.Any::class] as ReflektClass<in
                org.jetbrains.reflekt.test.common.BInterface>
        (m[org.jetbrains.reflekt.test.common.B2::class]!! as
                ReflektClassImpl<org.jetbrains.reflekt.test.common.B2>).superclasses +=
                m[org.jetbrains.reflekt.test.common.BInterface::class] as ReflektClass<in
                org.jetbrains.reflekt.test.common.B2>
        reflektClasses = m
    }

    public fun objects() = Objects()

    public fun classes() = Classes()

    public fun functions() = Functions()

    public class Objects {
        public fun <T : Any> withSuperTypes(fqNames: Set<String>) = WithSuperTypes<T>(fqNames)

        public fun <T : Any> withAnnotations(annotationFqNames: Set<String>,
                supertypeFqNames: Set<String>) = WithAnnotations<T>(annotationFqNames,
                supertypeFqNames)

        public class WithSuperTypes<T : Any>(
            public val fqNames: Set<String>,
        ) {
            public fun toList(): List<ReflektClass<T>> = emptyList()

            public fun toSet(): Set<ReflektClass<T>> = toList().toSet()
        }

        public class WithAnnotations<T : Any>(
            public val annotationFqNames: Set<String>,
            public val supertypeFqNames: Set<String>,
        ) {
            public fun toList(): List<ReflektClass<T>> = when (annotationFqNames) {
                else -> emptyList()
            }

            public fun toSet(): Set<ReflektClass<T>> = toList().toSet()
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
            public fun toList(): List<ReflektClass<T>> = emptyList()

            public fun toSet(): Set<ReflektClass<T>> = toList().toSet()
        }

        public class WithAnnotations<T : Any>(
            public val annotationFqNames: Set<String>,
            public val supertypeFqNames: Set<String>,
        ) {
            public fun toList(): List<ReflektClass<T>> = when (annotationFqNames) {
                setOf("org.jetbrains.reflekt.test.common.FirstAnnotation") -> {
                    when (supertypeFqNames) {
                        setOf("org.jetbrains.reflekt.test.common.B2") ->
                                listOf(reflektClasses[org.jetbrains.reflekt.test.common.B2::class]
                                as ReflektClass<T>)
                        else -> emptyList()
                    }
                }
                else -> emptyList()
            }

            public fun toSet(): Set<ReflektClass<T>> = toList().toSet()
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
