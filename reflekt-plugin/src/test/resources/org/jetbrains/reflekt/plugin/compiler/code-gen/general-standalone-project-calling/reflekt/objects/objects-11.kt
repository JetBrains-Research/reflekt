// FILE: TestCase.kt
import org.jetbrains.reflekt.Reflekt
import org.jetbrains.reflekt.test.helpers.*
import org.jetbrains.reflekt.test.common.*

fun box(): String = checkObjectsCallResult(
    { Reflekt.objects().withAnnotations<AInterface>(FirstAnnotation::class, SecondAnnotation::class).toList() },
    listOf(expectedReflektClass[A2::class]!!, expectedReflektClass[A3::class]!!),
)
