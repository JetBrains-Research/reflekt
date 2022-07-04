// FILE: Annotations.kt
annotation class FirstAnnotation

annotation class SecondAnnotation(val message: String)

// FILE: Interfaces.kt
interface AInterface {
    fun description(): String
}

interface AInterface1: AInterface {
    override fun description(): String
}

interface BInterface

// FILE: Objects.kt
object A1: AInterface {
    override fun description(): String {
        return "HELLO A1"
    }
}

@SecondAnnotation("Test")
object A2: AInterface {
    override fun description(): String {
        return "HELLO A2"
    }
}

@FirstAnnotation
object A3: AInterface {
    override fun description(): String {
        return "HELLO A3"
    }
}

@SecondAnnotation("Test")
object A4: BInterface

// FILE: TestCase.kt
import org.jetbrains.reflekt.Reflekt
import org.jetbrains.reflekt.test.helpers.checkObjectsCallResult

fun box(): String = checkObjectsCallResult(
    { Reflekt.objects().withSuperType<AInterface>().withAnnotations<AInterface>(FirstAnnotation::class, SecondAnnotation::class).toList() },
    listOf("A2", "A3")
)

