// FILE: TestCase.kt
import org.jetbrains.reflekt.SmartReflekt
import org.jetbrains.reflekt.test.helpers.checkFinctionsCallResult

fun box(): String = checkFinctionsCallResult(
    { SmartReflekt.functions<() -> Unit>().filter { it.name.asString() == "foo" }.resolve() },
    emptyList(),
)
