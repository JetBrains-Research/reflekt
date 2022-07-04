// FILE: TestCase.kt
import org.jetbrains.reflekt.Reflekt
import org.jetbrains.reflekt.helpers.checkCallResult

fun box(): String = checkCallResult(
    { Reflekt.objects().withSuperType<AInterface>().withAnnotations<AInterface>(FirstAnnotation::class).toList() },
    listOf("A", "B")
)
