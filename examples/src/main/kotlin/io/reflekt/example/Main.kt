package io.reflekt.example

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSubType<AInterface>().withAnnotation<AInterface1>(FirstAnnotation::class)
    val objects1 = Reflekt.objects().withSubType<AInterface>()
        .withAnnotations<AInterface>(FirstAnnotation::class, SecondAnnotation::class)
    val objects2 = Reflekt.objects().withSubTypes<AInterface>(AInterface::class, A1::class)
        .withAnnotations<AInterface>(FirstAnnotation::class, SecondAnnotation::class)
    val objects3 = Reflekt.objects().withSubTypes<AInterface>(AInterface::class, A1::class)
        .withAnnotation<AInterface>(FirstAnnotation::class)
    val objects4 = Reflekt.objects().withAnnotation<AInterface>(FirstAnnotation::class)
    val objects5 = Reflekt.objects().withAnnotations<AInterface>(FirstAnnotation::class)
    val objects6 = Reflekt.objects().withAnnotations<A1>(FirstAnnotation::class).withSubType<AInterface>()
    val objects7 = Reflekt.objects().withAnnotations<A1>(FirstAnnotation::class).withSubTypes<A1>(AInterface::class)
}
