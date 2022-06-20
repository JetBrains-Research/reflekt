// FILE: TestCase.kt
import org.jetbrains.reflekt.SmartReflekt
import org.jetbrains.reflekt.test.helpers.checkFunctionsCallResult

fun box(): String = checkFunctionsCallResult(
    { SmartReflekt.functions<() -> Unit>().filter { it.name.asString() == "foo" }.resolve() },
    emptyList(),
)
