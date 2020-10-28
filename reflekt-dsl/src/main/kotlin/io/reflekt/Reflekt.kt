package io.reflekt

import kotlin.reflect.KClass
import kotlin.reflect.KFunction

object Reflekt {
    class Objects {
        // T - returned class
        inline fun <reified T: Any> withSubType() = WithSubTypes<T>(setOf(T::class.qualifiedName!!))
        fun withSubTypes(vararg klasses: KClass<out Any>) = WithSubTypes<Any>(klasses.mapNotNull { it.qualifiedName }.toSet())

        // T - returned class
        inline fun <reified T: Any> withAnnotations(vararg klasses: KClass<out Annotation>) =
            WithAnnotations<T>(klasses.mapNotNull { it.qualifiedName }.toSet(), setOf(T::class.qualifiedName!!))

        class WithSubTypes<T: Any>(val fqNames: Set<String>) {
            fun toList(): List<T> = ReflektImpl.objects().withSubTypes<T>(fqNames).toList()
            fun toSet(): Set<T> = toList().toSet()

            // T - returned class
            inline fun <reified Q: T> withAnnotations(vararg klasses: KClass<out Annotation>) =
                WithAnnotations<Q>(klasses.mapNotNull { it.qualifiedName }.toSet(), fqNames)
        }

        // T - returned class
        class WithAnnotations<T: Any>(private val annotationFqNames: Set<String>, private val subtypeFqNames: Set<String>) {
            fun toList(): List<T> = ReflektImpl.objects().withAnnotations<T>(annotationFqNames, subtypeFqNames).toList()
            fun toSet(): Set<T> = toList().toSet()

            // T - returned class
            inline fun <reified T: Any> withSubType() = WithSubTypes<T>(setOf(T::class.qualifiedName!!))
            fun withSubTypes(vararg klasses: KClass<out Any>) = WithSubTypes<Any>(klasses.mapNotNull { it.qualifiedName }.toSet())
        }
    }

    class Classes {
        // T - returned class
        inline fun <reified T: Any> withSubType() = WithSubTypes<T>(setOf(T::class.qualifiedName!!))
        fun withSubTypes(vararg klasses: KClass<out Any>) = WithSubTypes<Any>(klasses.mapNotNull { it.qualifiedName }.toSet())

        // T - returned class
        inline fun <reified T: Any> withAnnotations(vararg klasses: KClass<out Annotation>) =
            WithAnnotations<T>(klasses.mapNotNull { it.qualifiedName }.toSet(), setOf(T::class.qualifiedName!!))

        class WithSubTypes<T: Any>(val fqNames: Set<String>) {
            fun toList(): List<KClass<T>> = ReflektImpl.classes().withSubTypes<T>(fqNames).toList()
            fun toSet(): Set<KClass<T>> = toList().toSet()

            // T - returned class
            inline fun <reified Q: T> withAnnotations(vararg klasses: KClass<out Annotation>) =
                WithAnnotations<Q>(klasses.mapNotNull { it.qualifiedName }.toSet(), fqNames)
        }

        // T - returned class
        class WithAnnotations<T: Any>(private val annotationFqNames: Set<String>, val subtypeFqNames: Set<String>) {
            fun toList(): List<KClass<T>> = ReflektImpl.classes().withAnnotations<T>(annotationFqNames, subtypeFqNames).toList()
            fun toSet(): Set<KClass<T>> = toList().toSet()

            // T - returned class
            inline fun <reified T: Any> withSubType() = WithSubTypes<T>(subtypeFqNames)
            fun withSubTypes(vararg klasses: KClass<out Any>) = WithSubTypes<Any>(klasses.mapNotNull { it.qualifiedName }.toSet())
        }
    }

    class Functions {
        // T - returned class
        class WithAnnotations<T: Any>(private val annotationFqNames: Set<String>) {
            fun toList(): List<KFunction<T>> = ReflektImpl.functions().withAnnotations<T>(annotationFqNames).toList()
            fun toSet(): Set<KFunction<T>> = toList().toSet()
        }

        // T - returned class
        inline fun <reified T: Any> withAnnotations(vararg klasses: KClass<out Annotation>) = WithAnnotations<T>(klasses.mapNotNull { it.qualifiedName }.toSet())
    }

    fun objects() = Objects()
    fun classes() = Classes()
    fun functions() = Functions()
}
