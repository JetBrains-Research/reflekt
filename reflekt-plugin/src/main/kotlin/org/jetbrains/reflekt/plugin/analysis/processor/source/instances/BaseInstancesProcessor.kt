package org.jetbrains.reflekt.plugin.analysis.processor.source.instances

import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.reflekt.plugin.analysis.processor.FileID
import org.jetbrains.reflekt.plugin.analysis.processor.source.Processor

abstract class BaseInstancesProcessor<Output : Any>(override val binding: BindingContext) : Processor<Output>(binding) {
    // Store instances by file
    abstract val fileToInstances: HashMap<FileID, Output>
}
