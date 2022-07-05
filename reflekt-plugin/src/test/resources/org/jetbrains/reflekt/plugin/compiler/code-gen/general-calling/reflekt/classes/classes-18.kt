// FILE: TestCase.kt
import org.jetbrains.reflekt.Reflekt
import org.jetbrains.reflekt.test.helpers.checkClassesCallResult
import org.jetbrains.reflekt.test.common.*

fun box(): String = checkClassesCallResult(
    { Reflekt.classes().withSuperTypes(B3::class).withAnnotations<B3>().toList() },
    listOf("B3"),
    "org.jetbrains.reflekt.test.common",
)
