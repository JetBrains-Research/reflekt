package io.reflekt.plugin.ic.modification.actions

import java.io.File

interface IEditAction {
    fun apply(file: File): File?
}

sealed class EditAction : IEditAction

object DeleteFile : EditAction() {
    override fun apply(file: File): File? {
        file.delete()
        return null
    }
}

class RenameFile(private val newName: String) : EditAction() {
    override fun apply(file: File): File {
        val dest = File(file.parentFile, "$newName.${file.extension}")
        file.renameTo(dest)
        return dest
    }
}
