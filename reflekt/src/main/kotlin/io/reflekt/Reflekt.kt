package io.reflekt

import kotlin.reflect.KClass

object Reflekt {
    class Objects {
        fun <T> withSubType() = Objects.WithSubType<T>()

        class WithSubType<T> {
            fun toList(): List<T> = ReflektImpl.objects().withSubType<T>().toList()
            fun toSet(): Set<T> = toList().toSet()
        }
    }

    class Classes {
        fun <T: Any> withSubType() = Classes.WithSubType<T>()

        class WithSubType<T: Any> {
            fun toList(): List<KClass<T>> = ReflektImpl.classes().withSubType<T>().toList()
            fun toSet(): Set<KClass<T>> = toList().toSet()
        }
    }

    fun objects() = Objects()

    fun classes() = Classes()
}

