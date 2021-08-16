package io.reflekt.example

import com.github.gumtreediff.actions.model.Action
import io.reflekt.Reflekt
import io.reflekt.SmartReflekt

class Test(var a: List<Any>, b: List<Any>)

fun main() {
    val tmp = Test(emptyList(), emptyList())
    tmp.a = Reflekt.objects().withSupertype<AInterface>().withAnnotations<AInterface>(FirstAnnotation::class).toList()
    println(tmp.a)

    val objects = Reflekt.objects().withSupertype<AInterface>().withAnnotations<AInterface>(FirstAnnotation::class, SecondAnnotation::class).toList()
    println(objects)
    val objects1 = Reflekt.objects().withSupertype<AInterface>()
        .withAnnotations<AInterface>(FirstAnnotation::class, SecondAnnotation::class).toList()
    println(objects1)

    val objects2 = Reflekt.objects().withSupertypes(AInterface::class, A1::class)
        .withAnnotations<AInterface>(FirstAnnotation::class, SecondAnnotation::class).toList()
    println(objects2)

    val objects3 = Reflekt.objects().withSupertypes(AInterface::class, A1::class)
        .withAnnotations<AInterface>(FirstAnnotation::class).toList()
    println(objects3)

    val objects4 = Reflekt.objects().withAnnotations<AInterface>(FirstAnnotation::class).toList()
    println(objects4)
    val objects5 = Reflekt.objects().withAnnotations<AInterface>(FirstAnnotation::class).toList()
    println(objects5)
    val objects6 = Reflekt.objects().withAnnotations<A1>(FirstAnnotation::class).withSupertype<AInterface>().toList()
    println(objects6)
    val objects7 = Reflekt.objects().withAnnotations<A1>(FirstAnnotation::class).withSupertypes(AInterface::class).toList()
    println(objects7)
    val objects8 = Reflekt.objects().withSupertype<AInterface>().toList()
    println(objects8)

    val classes1 = Reflekt.classes().withSupertype<AInterface>().toList()
    println(classes1)
    val classes2 = Reflekt.classes().withSupertype<BInterface>().toSet()
    println(classes2)
    val classes3 = Reflekt.classes().withAnnotations<B2>(FirstAnnotation::class, SecondAnnotation::class).toList()
    println(classes3)

    val classes4 = Reflekt.classes().withSupertype<Action>().toList()
    println(classes4)

    val functions = Reflekt.functions().withAnnotations<() -> Unit>(FirstAnnotation::class).toList()
    println(functions)

    val smartClasses = SmartReflekt.classes<BInterface>().filter { it.isData() }.resolve()
    println(smartClasses)

    val smartFunctions = SmartReflekt.functions<() -> Unit>().filter { it.isTopLevel && it.name == "foo" }.resolve()
    println(smartFunctions)
    smartFunctions.forEach { it() }
}
