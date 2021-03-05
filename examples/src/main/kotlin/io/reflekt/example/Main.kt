package io.reflekt.example

import com.github.gumtreediff.actions.model.Action
import io.reflekt.Reflekt
import io.reflekt.SmartReflekt
import com.google.devtools.ksp.symbol.ClassKind

class Test(var a: List<Any>, b: List<Any>)

fun main() {
    val tmp = Test(emptyList(), emptyList())
    tmp.a = listOf(Reflekt.objects().withSubType<AInterface>().withAnnotations<AInterface>(FirstAnnotation::class))

    val objects = Reflekt.objects().withSubType<AInterface>().withAnnotations<AInterface>(FirstAnnotation::class, SecondAnnotation::class).toList()
    println(objects)
    val objects1 = Reflekt.objects().withSubType<AInterface>()
        .withAnnotations<AInterface>(FirstAnnotation::class, SecondAnnotation::class).toList()
    println(objects1)

    val objects2 = Reflekt.objects().withSubTypes(AInterface::class, A1::class)
        .withAnnotations<AInterface>(FirstAnnotation::class, SecondAnnotation::class).toList()
    println(objects2)

    val objects3 = Reflekt.objects().withSubTypes(AInterface::class, A1::class)
        .withAnnotations<AInterface>(FirstAnnotation::class).toList()
    println(objects3)

    val objects4 = Reflekt.objects().withAnnotations<AInterface>(FirstAnnotation::class).toList()
    println(objects4)
    val objects5 = Reflekt.objects().withAnnotations<AInterface>(FirstAnnotation::class).toList()
    println(objects5)
    val objects6 = Reflekt.objects().withAnnotations<A1>(FirstAnnotation::class).withSubType<AInterface>().toList()
    println(objects6)
    val objects7 = Reflekt.objects().withAnnotations<A1>(FirstAnnotation::class).withSubTypes(AInterface::class).toList()
    println(objects7)
    val objects8 = Reflekt.objects().withSubType<AInterface>().toList()
    println(objects8)

    val classes1 = Reflekt.classes().withSubType<AInterface>().toList()
    println(classes1)
    val classes2 = Reflekt.classes().withSubType<BInterface>().toList()
    println(classes2)
    val classes3 = Reflekt.classes().withAnnotations<B2>(FirstAnnotation::class, SecondAnnotation::class).toList()
    println(classes3)

    val classes4 = Reflekt.classes().withSubType<Action>().toList()
    println(classes4)

//    val smartClasses = SmartReflekt.classes<AInterface>().filter { it.classKind == ClassKind.INTERFACE }.resolve()
//    println(smartClasses)

    // TODO: add more examples with functions
//    val functions = Reflekt.functions().withAnnotations<() -> Unit>().toList()
//    println(functions)
}
