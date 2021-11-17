package io.reflekt.plugin.analysis.processor.source.instances

import io.reflekt.plugin.analysis.processor.FileId
import io.reflekt.plugin.analysis.processor.source.Processor
import org.jetbrains.kotlin.resolve.BindingContext

typealias FileToListInstances<T> = HashMap<FileId, MutableList<T>>

/**
 * @property binding
 */
abstract class BaseInstancesProcessor<T : Any>(override val binding: BindingContext) : Processor<T>(binding) {
    // Store instances by file
    abstract val fileToInstances: HashMap<FileId, T>
}
