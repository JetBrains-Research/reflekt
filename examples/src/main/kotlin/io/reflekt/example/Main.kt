package io.reflekt.example

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSubType<AInterface>().toList()
    println(objects.joinToString { it.description() })


    val classes = Reflekt.classes().withSubType<BInterface>().toList()
    println(classes.joinToString { it.toString() })
}
