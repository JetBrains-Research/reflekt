// FILE: TestCase.kt
import org.jetbrains.reflekt.*
import org.jetbrains.reflekt.test.helpers.checkClassesCallResult
import org.jetbrains.reflekt.test.common.*

fun box(): String = checkClassesCallResult(
    { Reflekt.classes().withSuperType<B2>().toList() },
    listOf(expectedReflektClass[B2::class]!!),
)
