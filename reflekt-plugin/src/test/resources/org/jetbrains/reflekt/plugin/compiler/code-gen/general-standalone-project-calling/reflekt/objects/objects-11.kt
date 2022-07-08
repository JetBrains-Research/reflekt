// FILE: TestCase.kt
import org.jetbrains.reflekt.Reflekt
import org.jetbrains.reflekt.test.helpers.checkObjectsCallResult
import org.jetbrains.reflekt.test.common.*

fun box(): String = checkObjectsCallResult(
    { Reflekt.objects().withAnnotations<AInterface>(FirstAnnotation::class, SecondAnnotation::class).toList() },
    listOf("A2", "A3"),
    "org.jetbrains.reflekt.test.common",
)
