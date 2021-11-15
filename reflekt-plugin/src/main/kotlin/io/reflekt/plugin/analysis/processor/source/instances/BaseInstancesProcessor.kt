package io.reflekt.plugin.analysis.processor.source.instances

import io.reflekt.plugin.analysis.processor.FileId
import io.reflekt.plugin.analysis.processor.source.Processor
import org.jetbrains.kotlin.resolve.BindingContext

abstract class BaseInstancesProcessor<Output : Any>(override val binding: BindingContext) : Processor<Output>(binding) {
    // Store instances by file
    abstract val fileToInstances: HashMap<FileId, Output>
}
