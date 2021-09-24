package io.reflekt.plugin.ic.modification

import io.reflekt.plugin.ic.modification.actions.EditAction
import java.io.File

data class Modification(
    private val file: File,
    private val actions: List<EditAction>
) {
    fun applyActions(): File? {
        var currentFile: File? = file
        actions.forEach {
            require(currentFile != null) { "The current file is null, but were not apllied all edit actions" }
            currentFile = it.apply(currentFile!!)
        }
        return currentFile
    }
}
