package org.jetbrains.reflekt.plugin.analysis.processor.descriptor

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor

abstract class DescriptorProcessor<Output : Any>(protected open val messageCollector: MessageCollector? = null) {
    // Return processed elements by file
    abstract fun process(descriptor: DeclarationDescriptor): Output

    abstract fun shouldRunOn(descriptor: DeclarationDescriptor): Boolean
}
