// FILE: TestCase.kt
import org.jetbrains.reflekt.SmartReflekt
import org.jetbrains.reflekt.test.helpers.*
import org.jetbrains.reflekt.test.common.*

fun box(): String = checkClassesCallResult(
    { SmartReflekt.classes<AInterface>().filter { it.isData }.resolve() },
    emptyList(),
)
