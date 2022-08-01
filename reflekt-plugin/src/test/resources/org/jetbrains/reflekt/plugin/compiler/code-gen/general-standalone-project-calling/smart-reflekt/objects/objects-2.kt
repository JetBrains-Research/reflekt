// FILE: TestCase.kt
import org.jetbrains.reflekt.SmartReflekt
import org.jetbrains.reflekt.test.helpers.*
import org.jetbrains.reflekt.test.common.*

fun box(): String = checkObjectsCallResult(
    {  SmartReflekt.objects<Any>().filter { it.isCompanion }.resolve() },
    listOf(expectedReflektClass[TestFunctions.Companion::class]!!),
)
