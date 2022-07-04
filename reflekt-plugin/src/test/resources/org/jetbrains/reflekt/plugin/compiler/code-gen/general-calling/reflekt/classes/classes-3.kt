// FILE: TestCase.kt
import org.jetbrains.reflekt.Reflekt
import org.jetbrains.reflekt.test.helpers.checkCallResult
import org.jetbrains.reflekt.example.*


fun box(): String = checkCallResult(
    { Reflekt.classes().withAnnotations<B2>(FirstAnnotation::class, SecondAnnotation::class).toList() },
    listOf("A", "B")
)
