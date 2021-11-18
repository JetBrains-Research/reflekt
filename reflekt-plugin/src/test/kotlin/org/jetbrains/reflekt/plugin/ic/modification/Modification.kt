package org.jetbrains.reflekt.plugin.ic.modification

import org.jetbrains.reflekt.plugin.ic.modification.actions.EditAction
import java.io.File

data class Modification(
    private val file: File,
    private val actions: List<EditAction>
) {
    fun applyActions(): File? {
        var currentFile: File? = file
        actions.forEach {
            require(currentFile != null) { "The current file is null, but not all edit actions have been applied" }
            currentFile = it.apply(currentFile!!)
        }
        return currentFile
    }
}

fun List<Modification>.applyModifications() {
    this.forEach { m ->
        m.applyActions()
    }
}
