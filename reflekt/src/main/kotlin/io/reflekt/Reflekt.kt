package io.reflekt

import kotlin.reflect.KClass

object Reflekt {
    class Objects {
        inline fun <reified T> withSubType() = Objects.WithSubType<T>(T::class.qualifiedName!!)

        class WithSubType<T>(private val fqName: String) {
            fun toList(): List<T> = ReflektImpl.objects().withSubType<T>(fqName).toList()
            fun toSet(): Set<T> = toList().toSet()
        }
    }

    class Classes {
        inline fun <reified T: Any> withSubType() = Classes.WithSubType<T>(T::class.qualifiedName!!)

        class WithSubType<T: Any>(private val fqName: String) {
            fun toList(): List<KClass<T>> = ReflektImpl.classes().withSubType<T>(fqName).toList()
            fun toSet(): Set<KClass<T>> = toList().toSet()
        }
    }

    fun objects() = Objects()

    fun classes() = Classes()
}

