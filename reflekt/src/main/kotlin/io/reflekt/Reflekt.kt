package io.reflekt

import kotlin.reflect.KClass

object Reflekt {
    class Objects {
        fun <T> withSubType(fqName: String) = Objects.WithSubType<T>(fqName)

        class WithSubType<T>(private val fqName: String) {
            fun toList(): List<T> = ReflektImpl.objects().withSubType<T>(fqName).toList()
            fun toSet(): Set<T> = toList().toSet()
        }
    }

    class Classes {
        fun <T: Any> withSubType(fqName: String) = Classes.WithSubType<T>(fqName)

        class WithSubType<T: Any>(private val fqName: String) {
            fun toList(): List<KClass<T>> = ReflektImpl.classes().withSubType<T>(fqName).toList()
            fun toSet(): Set<KClass<T>> = toList().toSet()
        }
    }

    fun objects() = Objects()

    fun classes() = Classes()
}

