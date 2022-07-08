// FILE: TestCase.kt
import org.jetbrains.reflekt.SmartReflekt
import org.jetbrains.reflekt.test.helpers.checkFinctionsCallResult

fun box(): String = checkFinctionsCallResult(
    { SmartReflekt.functions<() -> Unit>().filter { "foo" in it.name.asString() }.resolve() },
    listOf("fun foo1(): kotlin.Unit", "fun foo2(): kotlin.Unit", "fun foo4(): kotlin.Unit"),
)
