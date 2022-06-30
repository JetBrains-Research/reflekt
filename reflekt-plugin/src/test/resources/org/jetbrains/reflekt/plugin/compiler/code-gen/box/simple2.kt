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

// вынести это в одну штуку куда-то мб?
fun box(): String {
    val objects = Reflekt.objects().withAnnotations<>(IrTestAnnotation::class).toList()
    val strRepresentation = objects.joinToString { it::class.qualifiedName ?: "Undefined name" }
    return if (strRepresentation == "A4") "OK"  else "Fail: $strRepresentation"
}
