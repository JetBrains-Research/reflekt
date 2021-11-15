package io.reflekt.plugin.analysis.processor.descriptor

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor

/**
 * @property messageCollector
 */
abstract class DescriptorProcessor<T : Any>(protected open val messageCollector: MessageCollector? = null) {
    // Return processed elements by file
    abstract fun process(descriptor: DeclarationDescriptor): T

    abstract fun shouldRunOn(descriptor: DeclarationDescriptor): Boolean
}
