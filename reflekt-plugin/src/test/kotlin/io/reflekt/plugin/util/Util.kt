package io.reflekt.plugin.util

import kotlin.reflect.KFunction

object Util {

    fun getResourcesRootPath(
        cls: KFunction<Any>,
        resourcesRootName: String = "data"
    ): String = cls.javaClass.getResource(resourcesRootName).path

}
