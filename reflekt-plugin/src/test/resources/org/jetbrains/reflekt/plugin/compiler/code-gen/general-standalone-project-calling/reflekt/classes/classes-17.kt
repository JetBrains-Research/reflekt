// FILE: TestCase.kt
import org.jetbrains.reflekt.Reflekt
import org.jetbrains.reflekt.test.helpers.*
import org.jetbrains.reflekt.test.common.*

fun box(): String = checkClassesCallResult(
    { Reflekt.classes().withSuperTypes().withAnnotations<B3>().toList() },
    listOf(expectedReflektClass[B3::class]!!),
)
