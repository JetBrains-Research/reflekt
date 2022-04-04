package org.jetbrains.reflekt.plugin.analysis.models.ir

import kotlinx.serialization.Serializable
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.reflekt.plugin.analysis.models.BaseReflektDataByFile
import org.jetbrains.reflekt.plugin.analysis.models.merge
import org.jetbrains.reflekt.plugin.analysis.models.psi.*
import org.jetbrains.reflekt.plugin.analysis.processor.FileId
import org.jetbrains.reflekt.plugin.analysis.serialization.SerializationUtils.toIrType
import org.jetbrains.reflekt.plugin.analysis.serialization.SerializationUtils.toSerializableIrType

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
        classes = classes.merge(second.classes) { mutableSetOf() },
        objects = objects.merge(second.objects) { mutableSetOf() },
        functions = functions.merge(second.functions) { mutableSetOf() },
    )

    fun toSerializableLibraryArguments() =
        SerializableReflektQueryArguments(
            objects = objects,
            classes = classes,
            functions = functions.mapValues { fileToArgs ->
                fileToArgs.value.map {
                    SerializableSignatureToAnnotations(
                        annotations = it.annotations,
                        irSignature = it.irSignature?.toSerializableIrType(),
                    )
                }.toMutableSet()
            } as HashMap,
        )
}

@Suppress("ConstructorParameterNaming")
data class LibraryArgumentsWithInstances(
    private var libraryArguments_: LibraryArguments = LibraryArguments(),
    private var instances_: IrInstancesFqNames = IrInstancesFqNames(),
) {
    val libraryArguments: LibraryArguments get() = libraryArguments_
    val instances: IrInstancesFqNames get() = instances_

    fun toSerializableLibraryArgumentsWithInstances() =
        SerializableLibraryArgumentsWithInstances(
            libraryArguments = libraryArguments.toSerializableLibraryArguments(),
            instances = instances,
        )

    fun replace(newLibraryArguments: LibraryArguments, newInstances: IrInstancesFqNames) {
        libraryArguments_ = newLibraryArguments
        instances_ = newInstances
    }
}

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
                functions = libraryArguments.functions.mapValues { fileToInvokes ->
                    fileToInvokes.value.map {
                        SignatureToAnnotations(
                            annotations = it.annotations,
                            irSignature = it.irSignature?.toIrType(pluginContext)
                        )
                    }.toMutableSet()
                } as HashMap,
            ),
            instances,
        )
}
