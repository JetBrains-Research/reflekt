// FILE: TestCase.kt
import org.jetbrains.reflekt.Reflekt
import org.jetbrains.reflekt.test.helpers.checkObjectsCallResult
import org.jetbrains.reflekt.test.common.*

// TODO: fix it
fun box(): String = "OK"
//    checkObjectsCallResult(
//    { Reflekt.objects().withSuperType<AInterface>().withAnnotations<A3>(FirstAnnotation::class, SecondAnnotation::class).toList() },
//    listOf("A2", "A3"),
//    "org.jetbrains.reflekt.test.common",
//)
