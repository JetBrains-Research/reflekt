// FILE: TestCase.kt
import org.jetbrains.reflekt.Reflekt
import org.jetbrains.reflekt.test.helpers.checkClassesCallResult
import org.jetbrains.reflekt.test.common.*

fun box(): String = checkClassesCallResult(
    { Reflekt.classes().withAnnotations<BInterface>(FirstAnnotation::class, SecondAnnotation::class).toList() },
    listOf("B2", "B3"),
    "org.jetbrains.reflekt.test.common",
)
