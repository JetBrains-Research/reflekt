package io.reflekt.plugin.analysis.processor.instances

import io.reflekt.plugin.analysis.processor.FileId
import io.reflekt.plugin.analysis.processor.Processor
import org.jetbrains.kotlin.resolve.BindingContext

abstract class BaseInstancesProcessor<T : Any>(override val binding: BindingContext) : Processor<T>(binding) {
    // Store instances by file
    abstract val fileToInstances: HashMap<FileId, T>
}
