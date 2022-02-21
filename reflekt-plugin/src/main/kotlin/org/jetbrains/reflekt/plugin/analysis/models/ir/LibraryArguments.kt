package org.jetbrains.reflekt.plugin.analysis.models.ir

import org.jetbrains.reflekt.plugin.analysis.analyzer.ir.IrReflektQueriesAnalyzer
import org.jetbrains.reflekt.plugin.analysis.models.BaseReflektDataByFile
import org.jetbrains.reflekt.plugin.analysis.models.psi.SignatureToAnnotations
import org.jetbrains.reflekt.plugin.analysis.models.psi.SupertypesToAnnotations
import org.jetbrains.reflekt.plugin.analysis.processor.FileId
import org.jetbrains.reflekt.plugin.analysis.processor.ir.reflektArguments.ReflektArgumentsToFileSet

typealias LibraryArgumentsMap<T> = HashMap<FileId, MutableSet<T>>

/**
 * Stores all Reflekt queries arguments from the library.
 * @property classes
 * @property objects
 * @property functions
 */
data class LibraryArguments(
    override val classes: LibraryArgumentsMap<SupertypesToAnnotations> = HashMap(),
    override val objects: LibraryArgumentsMap<SupertypesToAnnotations> = HashMap(),
    override val functions: LibraryArgumentsMap<SignatureToAnnotations> = HashMap(),
) : BaseReflektDataByFile<MutableSet<SupertypesToAnnotations>, MutableSet<SupertypesToAnnotations>, MutableSet<SignatureToAnnotations>>(
    classes,
    objects,
    functions,
) {
    fun merge(second: LibraryArguments) = LibraryArguments(
        classes = this.classes.merge(second.classes),
        objects = this.objects.merge(second.objects),
        functions = this.functions.merge(second.functions),
    )

    companion object {
        fun fromIrReflektQueriesAnalyzer(analyzer: IrReflektQueriesAnalyzer) = LibraryArguments(
            classes = analyzer.classProcessor.collectedElements.toLibraryArgumentsMap(),
            objects = analyzer.objectProcessor.collectedElements.toLibraryArgumentsMap(),
            functions = analyzer.functionProcessor.collectedElements.toLibraryArgumentsMap(),
        )

        @Suppress("TYPE_ALIAS")
        private fun <R> ReflektArgumentsToFileSet<R>.toLibraryArgumentsMap(): LibraryArgumentsMap<R> {
            val entities: HashMap<FileId, MutableSet<R>> = HashMap()
            this.forEach { (args, files) ->
                files.forEach {
                    entities.getOrPut(it) { mutableSetOf() }.add(args)
                }
            }
            return entities
        }
    }
}

private fun <T> LibraryArgumentsMap<T>.merge(second: LibraryArgumentsMap<T>): LibraryArgumentsMap<T> {
    second.forEach { (file, res) ->
        this.getOrPut(file) { mutableSetOf() }.addAll(res)
    }
    return second
}
