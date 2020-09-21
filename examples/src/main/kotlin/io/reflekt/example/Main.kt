package io.reflekt.example

import io.reflekt.Reflekt

fun main() {
    val objects = Reflekt.objects().withSubType<AInterface>("io.reflekt.example.AInterface").toList()
    println(objects.joinToString { it.description() })


    val classes = Reflekt.classes().withSubType<BInterface>("io.reflekt.example.BInterface").toList()
    println(classes.joinToString { it.toString() })
}

// io.reflekt.Reflekt.Objects.withSubType
// io.reflekt.Reflekt.Classes.withSubType
