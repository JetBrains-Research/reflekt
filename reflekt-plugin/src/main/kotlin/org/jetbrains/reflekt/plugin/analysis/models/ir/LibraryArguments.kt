@file:Suppress("FILE_UNORDERED_IMPORTS")

package org.jetbrains.reflekt.plugin.analysis.models.ir

import kotlinx.serialization.Serializable
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.reflekt.plugin.analysis.models.*
import org.jetbrains.reflekt.plugin.analysis.processor.FileId
import org.jetbrains.reflekt.plugin.analysis.serialization.SerializationUtils.toIrType
import org.jetbrains.reflekt.plugin.analysis.serialization.SerializationUtils.toSerializableIrType

typealias LibraryArgumentsMap<T> = MutableMap<FileId, MutableSet<T>>
typealias TypeLibraryQueriesResults<K, V> = MutableMap<K, MutableSet<V>>
typealias ClassOrObjectLibraryQueriesResults = TypeLibraryQueriesResults<SupertypesToAnnotations, IrClass>
typealias FunctionLibraryQueriesResults = TypeLibraryQueriesResults<SignatureToAnnotations, IrFunction>

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
        classes = classes.merge(second.classes) { HashSet() },
        objects = objects.merge(second.objects) { HashSet() },
        functions = functions.merge(second.functions) { HashSet() },
    )

    fun toSerializableLibraryArguments() = SerializableReflektQueryArguments(
        objects = objects,
        classes = classes,
        functions = functions.mapValuesTo(HashMap()) { fileToArgs ->
            fileToArgs.value.map {
                SerializableSignatureToAnnotations(
                    annotations = it.annotations,
                    irSignature = it.irSignature?.toSerializableIrType(),
                )
            }.toHashSet()
        },
    )
}

/**
 * Stores all Reflekt queries arguments from the library with all instances of classes, objects, and functions.
 * @property libraryArguments_
 * @property instances_
 */
@Suppress("ConstructorParameterNaming")
data class LibraryArgumentsWithInstances(
    private var libraryArguments_: LibraryArguments = LibraryArguments(),
    private var instances_: IrInstancesFqNames = IrInstancesFqNames(),
) {
    val libraryArguments: LibraryArguments
        get() = libraryArguments_

    val instances: IrInstancesFqNames
        get() = instances_

    fun toSerializableLibraryArgumentsWithInstances() = SerializableLibraryArgumentsWithInstances(
        libraryArguments = libraryArguments.toSerializableLibraryArguments(),
        instances = instances,
    )

    fun replace(newLibraryArguments: LibraryArguments, newInstances: IrInstancesFqNames) {
        libraryArguments_ = newLibraryArguments
        instances_ = newInstances
    }
}

/**
 * Stores serializable [LibraryArgumentsWithInstances]:
 *  [libraryArguments] are replaced by [SerializableReflektQueryArguments] with serializable functions signatures.
 * @property libraryArguments
 * @property instances
 */
@Serializable
data class SerializableLibraryArgumentsWithInstances(
    val libraryArguments: SerializableReflektQueryArguments,
    val instances: IrInstancesFqNames,
) {
    fun toLibraryArgumentsWithInstances(pluginContext: IrPluginContext) =
        LibraryArgumentsWithInstances(
            LibraryArguments(
                objects = libraryArguments.objects,
                classes = libraryArguments.classes,
                functions = libraryArguments.functions.mapValuesTo(HashMap()) { fileToInvokes ->
                    fileToInvokes.value.map {
                        SignatureToAnnotations(
                            annotations = it.annotations,
                            irSignature = it.irSignature?.toIrType(pluginContext),
                        )
                    }.toHashSet()
                },
            ),
            instances,
        )
}

/**
 * Stores for each Reflekt query from libraries a set of [IrElement], that satisfies this query.
 *
 * @property classes
 * @property objects
 * @property functions
 * @property mentionedClasses Information about all resulting classes, their superclasses, etc.
 */
// TODO: think about name
data class LibraryQueriesResults(
    var classes: ClassOrObjectLibraryQueriesResults = LinkedHashMap(),
    var objects: ClassOrObjectLibraryQueriesResults = HashMap(),
    var functions: FunctionLibraryQueriesResults = HashMap(),
    var mentionedClasses: MutableSet<IrClass> = LinkedHashSet(),
) {
    fun merge(second: LibraryQueriesResults) {
        classes = classes.merge(second.classes) { HashSet() }
        objects = objects.merge(second.objects) { HashSet() }
        functions = functions.merge(second.functions) { HashSet() }
        mentionedClasses = mentionedClasses.toMutableSet().also { it += second.mentionedClasses }
    }

    companion object {
        fun fromLibraryArguments(libraryArguments: LibraryArguments) = LibraryQueriesResults(
            classes = libraryArguments.classes.flatten(),
            objects = libraryArguments.objects.flatten(),
            functions = libraryArguments.functions.flatten(),
        )

        @Suppress("IDENTIFIER_LENGTH", "TYPE_ALIAS")
        private fun <T, V : IrElement> LibraryArgumentsMap<T>.flatten(): TypeLibraryQueriesResults<T, V> {
            val queriesResults: TypeLibraryQueriesResults<T, V> = HashMap()
            for ((_, arguments) in this) {
                for (it in arguments) {
                    queriesResults.getOrPut(it) { HashSet() }
                }
            }
            return queriesResults
        }
    }
}

@Suppress("IDENTIFIER_LENGTH")
fun ClassOrObjectLibraryQueriesResults.toSupertypesToFqNamesMap() = this.map { (k, v) -> k.supertypes to v.mapNotNull { it.fqNameWhenAvailable } }.toMap()
