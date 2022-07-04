// FILE: TestCase.kt
import org.jetbrains.reflekt.Reflekt
import org.jetbrains.reflekt.test.helpers.checkCallResult
import org.jetbrains.reflekt.example.*


fun box(): String = checkCallResult(
    { Reflekt.objects().withSuperTypes(AInterface::class, A1::class).withAnnotations<AInterface>(FirstAnnotation::class).toList() },
    listOf("A", "B")
)
