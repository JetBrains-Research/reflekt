// FILE: TestCase.kt
import org.jetbrains.reflekt.Reflekt
import org.jetbrains.reflekt.test.helpers.checkFinctionsCallResult

fun box(): String = checkFinctionsCallResult(
    { Reflekt.functions().withAnnotations<() -> Unit>().toList() },
    listOf("fun foo1(): kotlin.Unit", "fun foo2(): kotlin.Unit", "fun foo4(): kotlin.Unit")
)
