package io.reflekt.resources.io.reflekt.plugin.analysis.commontestfiles.test.nested

import io.reflekt.test.FirstAnnotationTest
import io.reflekt.test.SecondAnnotationTest

@FirstAnnotationTest
@SecondAnnotationTest("test")
fun foo() {}
