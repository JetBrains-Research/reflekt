// FILE: TestCase.kt
import org.jetbrains.reflekt.Reflekt
import org.jetbrains.reflekt.test.helpers.checkCallResult
import org.jetbrains.reflekt.example.*


fun box(): String = checkCallResult(
    { Reflekt.objects().withSuperType<AInterface>().withAnnotations<AInterface>(FirstAnnotation::class, SecondAnnotation::class).toList() },
    listOf("A", "B")
)
