package io.reflekt.example

import io.reflekt.Reflekt

class Test(var a: List<Any>, b: List<Any>)

fun main() {
    val tmp = Test(emptyList(), emptyList())
    tmp.a = listOf(Reflekt.objects().withSubType<AInterface>().withAnnotations<AInterface1>(FirstAnnotation::class))

    val objects = Reflekt.objects().withSubType<AInterface>().withAnnotations<AInterface1>(FirstAnnotation::class, SecondAnnotation::class)
    val objects1 = Reflekt.objects().withSubType<AInterface>()
        .withAnnotations<AInterface>(FirstAnnotation::class, SecondAnnotation::class)

    val objects2 = Reflekt.objects().withSubTypes(AInterface::class, A1::class)
        .withAnnotations<AInterface>(FirstAnnotation::class, SecondAnnotation::class)

    val objects3 = Reflekt.objects().withSubTypes(AInterface::class, A1::class)
        .withAnnotations<AInterface>(FirstAnnotation::class)

    val objects4 = Reflekt.objects().withAnnotations<AInterface>(FirstAnnotation::class)
    val objects5 = Reflekt.objects().withAnnotations<AInterface>(FirstAnnotation::class)
    val objects6 = Reflekt.objects().withAnnotations<A1>(FirstAnnotation::class).withSubType<AInterface>()
    val objects7 = Reflekt.objects().withAnnotations<A1>(FirstAnnotation::class).withSubTypes(AInterface::class)
    val objects8 = Reflekt.objects().withSubType<AInterface>()

    val classes1 = Reflekt.classes().withSubType<AInterface>()
    val classes2 = Reflekt.classes().withSubType<BInterface>()
    val classes3 = Reflekt.classes().withAnnotations<B2>(FirstAnnotation::class, SecondAnnotation::class)

}
