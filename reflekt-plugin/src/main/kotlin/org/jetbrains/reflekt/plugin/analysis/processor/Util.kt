package org.jetbrains.reflekt.plugin.analysis.processor

import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.DescriptorVisibility
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.reflekt.ReflektVisibility

// TODO: Use this function instead KtFile.fullName in the future
internal val IrFile.fullName: FileId
    get() = this.fileEntry.getSourceRangeInfo(0, this.fileEntry.maxOffset).filePath

typealias FileId = String

internal fun DescriptorVisibility.toReflektVisibility() = when (this) {
    DescriptorVisibilities.PUBLIC -> ReflektVisibility.PUBLIC
    DescriptorVisibilities.PROTECTED -> ReflektVisibility.PROTECTED
    DescriptorVisibilities.INTERNAL -> ReflektVisibility.INTERNAL
    DescriptorVisibilities.PRIVATE -> ReflektVisibility.PRIVATE
    else -> null
}

internal fun <K, V : MutableSet<K>> getInvokesGroupedByFiles(fileToInvokes: Map<FileId, V>): MutableSet<K> =
    groupFilesByInvokes(fileToInvokes).keys.flatten().toHashSet()

// To avoid repeated checks for belonging invokes in different files,
// we will group files by invokes and process each of them once
// MutableSet<*> here is ClassOrObjectInvokes = MutableSet<SupertypesToAnnotations>
// or FunctionInvokes = MutableSet<SignatureToAnnotations> MutableSet<*>
private fun <T : MutableSet<*>> groupFilesByInvokes(fileToInvokes: Map<FileId, T>): MutableMap<T, MutableSet<String>> {
    val filesByInvokes = HashMap<T, MutableSet<FileId>>()
    for ((file, invoke) in fileToInvokes) {
        filesByInvokes.getOrPut(invoke) { HashSet() }.add(file)
    }
    return filesByInvokes
}
