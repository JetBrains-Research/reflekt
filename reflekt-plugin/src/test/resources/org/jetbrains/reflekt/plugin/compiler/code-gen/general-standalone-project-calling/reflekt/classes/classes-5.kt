// FILE: TestCase.kt
import org.jetbrains.reflekt.*
import org.jetbrains.reflekt.test.helpers.checkClassesCallResultDetailed
import org.jetbrains.reflekt.test.common.*

@OptIn(InternalReflektApi::class)
fun box(): String {
    val any = ReflektClassImpl(
        kClass = Any::class,
        isFinal = false,
        isOpen = true,
        qualifiedName = "kotlin.Any",
        simpleName = "Any",
    )
    val binterface = ReflektClassImpl(
        kClass = BInterface::class,
        isAbstract = true,
        isFinal = false,
        qualifiedName = "org.jetbrains.reflekt.test.common.BInterface",
        simpleName = "BInterface",
    )
    val b2 = ReflektClassImpl(
        kClass = B2::class,
        annotations = hashSetOf(
            SecondAnnotation(
                message = "Test",
                first = FirstAnnotation(int = 42, array = booleanArrayOf(false, true))
            ),
            FirstAnnotation(int = 42, array = booleanArrayOf())
        ),
        isData = true,
        qualifiedName = "org.jetbrains.reflekt.test.common.B2",
        simpleName = "B2",
    )
    b2.superclasses += binterface
    binterface.superclasses += any

    return checkClassesCallResultDetailed(
        { Reflekt.classes().withSuperType<B2>().toList() },
        listOf(b2),
    )
}
