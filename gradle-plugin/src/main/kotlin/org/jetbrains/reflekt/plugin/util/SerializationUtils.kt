package org.jetbrains.reflekt.plugin.util

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import org.jetbrains.reflekt.plugin.ReflektMetaFilesFromLibrariesMap
import java.io.File

internal typealias SerializableReflektMetaFilesFromLibrariesMap = HashMap<String, Set<String>>

@Suppress("UnnecessaryOptInAnnotation")
@OptIn(ExperimentalSerializationApi::class)
internal object SerializationUtils {
    private const val RELATIVE_CACHE_FILE_NAME = "reflektMetaFilesFromLibrariesMap"
    private const val RELATIVE_CACHE_FOLDER_NAME = "reflekt-cache"
    private val protoBuf = ProtoBuf

    private fun SerializableReflektMetaFilesFromLibrariesMap.toReflektMetaFilesFromLibrariesMap() =
        HashMap(this.mapValues { (_, v) -> v.map { File(it) }.toSet() })

    private fun ReflektMetaFilesFromLibrariesMap.toSerializableReflektMetaFilesFromLibrariesMap() =
        HashMap(this.mapValues { (_, v) -> v.map { it.path }.toSet() })

    private fun getAbsoluteCacheFolderPath(buildDir: String) = "$buildDir/$RELATIVE_CACHE_FOLDER_NAME"
    private fun getAbsoluteCacheFile(buildDir: String) = File("${getAbsoluteCacheFolderPath(buildDir)}/$RELATIVE_CACHE_FILE_NAME")

    fun serializeReflektMetaFilesFromLibrariesMap(map: ReflektMetaFilesFromLibrariesMap, buildDir: String) {
        File(getAbsoluteCacheFolderPath(buildDir)).mkdirs()
        val file = getAbsoluteCacheFile(buildDir)
        file.createNewFile()
        file.writeBytes(protoBuf.encodeToByteArray(map.toSerializableReflektMetaFilesFromLibrariesMap()))
    }

    fun deserializeReflektMetaFilesFromLibrariesMap(buildDir: String): ReflektMetaFilesFromLibrariesMap {
        val file = getAbsoluteCacheFile(buildDir)
        if (!file.exists()) {
            return hashMapOf()
        }
        return protoBuf.decodeFromByteArray<SerializableReflektMetaFilesFromLibrariesMap>(file.readBytes()).toReflektMetaFilesFromLibrariesMap()
    }
}
