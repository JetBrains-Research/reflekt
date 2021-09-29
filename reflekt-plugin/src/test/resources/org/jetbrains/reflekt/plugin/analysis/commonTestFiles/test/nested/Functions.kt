package org.jetbrains.reflekt.test.nested

import org.jetbrains.reflekt.test.FirstAnnotationTest
import org.jetbrains.reflekt.test.SecondAnnotationTest

@FirstAnnotationTest
@SecondAnnotationTest("test")
fun foo() {}
