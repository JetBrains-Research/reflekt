package io.reflekt.test.nested

import io.reflekt.test.FirstAnnotationTest
import io.reflekt.test.SecondAnnotationTest

@FirstAnnotationTest
@SecondAnnotationTest("test")
fun foo() {}
