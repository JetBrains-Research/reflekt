package io.reflekt.plugin.analysis.processor.uses

import io.reflekt.plugin.analysis.processor.Processor
import org.jetbrains.kotlin.resolve.BindingContext

abstract class BaseUsesProcessor<Output : Any>(override val binding: BindingContext): Processor<Output>(binding) {
    abstract val uses: Output
}
