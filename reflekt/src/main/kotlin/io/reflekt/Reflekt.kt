package io.reflekt

import kotlin.reflect.KClass

object Reflekt {
    class Objects {
        // T - returned class
        // TODO: delete withSubType? withSubTypes works with empty args
        inline fun <reified T: Any> withSubType() = withSubTypes<T>(T::class)
        inline fun <reified T: Any> withSubTypes(vararg klasses: KClass<out Any>) = WithSubTypes<T>(klasses.mapNotNull { it.qualifiedName }.toSet())

        // T - returned class
        inline fun <reified T: Any> withAnnotation(klass: KClass<out Annotation>) = withAnnotations<T>(klass)
        inline fun <reified T: Any> withAnnotations(vararg klasses: KClass<out Annotation>) = WithAnnotations<T>(klasses.mapNotNull { it.qualifiedName }.toSet(), T::class.qualifiedName!!)

        class WithSubTypes<T: Any>(val fqNames: Set<String>) {
            fun toList(): List<T> = ReflektImpl.objects().withSubTypes<T>(fqNames).toList()
            fun toSet(): Set<T> = toList().toSet()

            // T - returned class
            // TODO: How can I delete inline reified and send T::class.qualifiedName!! without it??
            inline fun <reified Q: T> withAnnotation(klass: KClass<out Annotation>) = withAnnotations<Q>(klass)
            inline fun <reified Q: T> withAnnotations(vararg klasses: KClass<out Annotation>) = WithAnnotations<Q>(klasses.mapNotNull { it.qualifiedName }.toSet(), Q::class.qualifiedName!!)
        }

        // T - returned class
        class WithAnnotations<T: Any>(private val annotationFqNames: Set<String>, private val subtypeFqName: String) {
            fun toList(): List<T> = ReflektImpl.objects().withAnnotations<T>(annotationFqNames, subtypeFqName).toList()
            fun toSet(): Set<T> = toList().toSet()

            // T - returned class
            inline fun <reified T: Any> withSubType() = withSubTypes<T>(T::class)
            inline fun <reified T: Any> withSubTypes(vararg klasses: KClass<out Any>) = WithSubTypes<T>(klasses.mapNotNull { it.qualifiedName }.toSet())
        }
    }

    class Classes {
        inline fun <reified T: Any> withSubType() = WithSubType<T>(T::class.qualifiedName!!)

        class WithSubType<T: Any>(val fqName: String) {
            class WithAnnotation<T: Annotation>(private val fqName: String, private val withSubtype: ReflektImpl.Classes.WithSubType<T>) {
                fun toList(): List<T> = withSubtype.withAnnotation<T>(fqName, withSubtype.fqName).toList()
                fun toSet(): Set<T> = toList().toSet()
            }
            inline fun <reified T: Annotation> withAnnotation() = WithAnnotation<T>(T::class.qualifiedName!!, ReflektImpl.classes().withSubType(fqName))

            fun toList(): List<KClass<T>> = ReflektImpl.classes().withSubType<T>(fqName).toList()
            fun toSet(): Set<KClass<T>> = toList().toSet()
        }
    }

    fun objects() = Objects()
    fun classes() = Classes()
}
