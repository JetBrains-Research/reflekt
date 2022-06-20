package org.jetbrains.reflekt.test.common

annotation class FirstAnnotation(val int: Int = 42, val array: BooleanArray = [])

annotation class SecondAnnotation(val message: String, val first: FirstAnnotation = FirstAnnotation())

annotation class MyAnnotation()
