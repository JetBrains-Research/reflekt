package org.jetbrains.reflekt.plugin.analysis

import org.jetbrains.reflekt.plugin.util.MavenLocalUtil.getReflektProjectJars
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class AnalysisSetupTest {

    @Test
    @Tag("analysis")
    fun `analysis setup test`() {
        /*
         * Just check if the all necessary ReflektJars exist
         */
        assertDoesNotThrow { getReflektProjectJars() }
    }
}
