package io.reflekt

import kotlin.reflect.KClass

object Reflekt {
    class Objects {
        inline fun <reified T> withSubType() = WithSubType<T>(T::class.qualifiedName!!)

        class WithSubType<T>(val fqName: String) {
            class WithAnnotation<T>(private val fqName: String, private val withSubtype: ReflektImpl.Objects.WithSubType<T>) {
                fun toList(): List<T> = withSubtype.withAnnotation<T>(fqName, withSubtype.fqName).toList()
                fun toSet(): Set<T> = toList().toSet()
            }
            inline fun <reified T: Annotation> withAnnotation() = WithAnnotation<T>(T::class.qualifiedName!!, ReflektImpl.objects().withSubType(fqName))

            fun toList(): List<T> = ReflektImpl.objects().withSubType<T>(fqName).toList()
            fun toSet(): Set<T> = toList().toSet()
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

