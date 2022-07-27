// FILE: TestCase.kt
import org.jetbrains.reflekt.Reflekt
import org.jetbrains.reflekt.test.helpers.*
import org.jetbrains.reflekt.test.common.*

fun box(): String = checkClassesCallResult(
    { Reflekt.classes().withSuperType<B2>().withAnnotations<B2>(FirstAnnotation::class, MyAnnotation::class).toList() },
    listOf(expectedReflektClass[B2::class]!!),
)
