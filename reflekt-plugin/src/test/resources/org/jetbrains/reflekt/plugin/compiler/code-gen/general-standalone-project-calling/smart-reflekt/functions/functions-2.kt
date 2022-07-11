// FILE: TestCase.kt
import org.jetbrains.reflekt.SmartReflekt
import org.jetbrains.reflekt.test.helpers.checkFunctionsCallResult

fun box(): String = checkFunctionsCallResult(
    { SmartReflekt.functions<() -> Unit>().filter { "foo" in it.name.asString() }.resolve() },
    listOf("fun foo1(): kotlin.Unit", "fun foo2(): kotlin.Unit", "fun foo4(): kotlin.Unit"),
)
