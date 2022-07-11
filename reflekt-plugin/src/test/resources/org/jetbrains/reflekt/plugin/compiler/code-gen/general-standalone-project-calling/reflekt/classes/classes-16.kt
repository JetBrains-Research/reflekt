// FILE: TestCase.kt
import org.jetbrains.reflekt.Reflekt
import org.jetbrains.reflekt.test.helpers.checkClassesCallResult
import org.jetbrains.reflekt.test.common.*

// TODO: should we keep only B2?
fun box(): String = checkClassesCallResult(
    { Reflekt.classes().withSuperType<BInterface>().withAnnotations<B2>(FirstAnnotation::class, MyAnnotation::class).toList() },
    listOf(expectedReflektClass[B2::class]!!, expectedReflektClass[B3::class]!!),
)
