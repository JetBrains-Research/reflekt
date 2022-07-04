// FILE: TestCase.kt
import org.jetbrains.reflekt.Reflekt
import org.jetbrains.reflekt.test.helpers.checkCallResult
import org.jetbrains.reflekt.example.*

fun box(): String = checkCallResult(
    { Reflekt.objects().withAnnotations<A1>(FirstAnnotation::class).withSupertype<AInterface>().toList() },
    listOf("A", "B")
)
