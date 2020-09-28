package io.reflekt.example

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSubType<AInterface>().withAnnotation<FirstAnnotation>().toList()
    val objects2 = Reflekt.objects().withSubType<AInterface>().withAnnotation<SecondAnnotation>().toList()
    val objects3 = Reflekt.objects().withSubType<BInterface>().withAnnotation<SecondAnnotation>().toList()
    println(objects.joinToString { it.toString() })
    println(objects2.joinToString { it.toString() })
    println(objects3.joinToString { it.toString() })


    val classes = Reflekt.classes().withSubType<BInterface>().toList()
    val classes1 = Reflekt.classes().withSubType<BInterface>().withAnnotation<SecondAnnotation>().toList()
    val classes2 = Reflekt.classes().withSubType<BInterface>().withAnnotation<FirstAnnotation>().toList()
    println(classes.joinToString { it.toString() })
    println(classes1.joinToString { it.toString() })
    println(classes2.joinToString { it.toString() })
}
