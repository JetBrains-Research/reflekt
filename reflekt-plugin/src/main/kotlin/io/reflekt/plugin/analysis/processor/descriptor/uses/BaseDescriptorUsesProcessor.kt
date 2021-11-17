package io.reflekt.plugin.analysis.processor.descriptor.uses

import io.reflekt.plugin.analysis.models.*
import io.reflekt.plugin.analysis.processor.descriptor.DescriptorProcessor
import io.reflekt.plugin.analysis.resolve.isSubtypeOf
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

/**
 * @property messageCollector
 */
abstract class BaseDescriptorUsesProcessor<T : Any>(override val messageCollector: MessageCollector?) :
    DescriptorProcessor<T>(messageCollector) {
    // Store uses by file
    abstract val uses: T

    protected fun processClassOrObjectUses(
        descriptor: DeclarationDescriptor,
        invokes: ClassOrObjectInvokes,
        uses: IrClassOrObjectUses,
    ): IrClassOrObjectUses {
        (descriptor as? ClassifierDescriptor)?.let {
            invokes.filter { it.isCovering(descriptor) }.forEach {
                uses.getOrPut(it) { mutableListOf() }.add(descriptor.fqNameSafe.asString())
            }
        }
        return uses
    }

    private fun SupertypesToAnnotations.isCovering(descriptor: ClassifierDescriptor): Boolean =
        // annotations set is empty when withSupertypes() method is called, so we don't need to check annotations in this case
        shouldCheckAnnotations(annotations, descriptor) && descriptor.isSubtypeOf(supertypes)

    protected fun shouldCheckAnnotations(annotations: Set<String>, descriptor: DeclarationDescriptor): Boolean =
        annotations.isEmpty() || descriptor.annotations.isEmpty()
}
