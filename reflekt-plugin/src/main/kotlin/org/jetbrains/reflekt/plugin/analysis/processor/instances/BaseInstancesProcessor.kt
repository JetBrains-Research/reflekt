package org.jetbrains.reflekt.plugin.analysis.processor.instances

import org.jetbrains.reflekt.plugin.analysis.processor.FileID
import org.jetbrains.reflekt.plugin.analysis.processor.Processor
import org.jetbrains.kotlin.resolve.BindingContext

abstract class BaseInstancesProcessor<Output : Any>(override val binding: BindingContext) : Processor<Output>(binding) {
    // Store instances by file
    abstract val fileToInstances: HashMap<FileID, Output>
}
