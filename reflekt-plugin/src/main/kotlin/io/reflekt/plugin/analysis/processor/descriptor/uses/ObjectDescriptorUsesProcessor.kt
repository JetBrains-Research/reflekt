package io.reflekt.plugin.analysis.processor.descriptor.uses

import io.reflekt.plugin.analysis.models.IrClassOrObjectUses
import io.reflekt.plugin.analysis.models.ReflektInvokes
import io.reflekt.plugin.analysis.processor.getInvokesGroupedByFiles
import io.reflekt.plugin.analysis.processor.isPublicObject
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor

/**
 * @param reflektInvokes
 *
 * @property messageCollector
 */
class ObjectDescriptorUsesProcessor(reflektInvokes: ReflektInvokes, override val messageCollector: MessageCollector?) :
    BaseDescriptorUsesProcessor<IrClassOrObjectUses>(messageCollector) {
    override val uses: IrClassOrObjectUses = HashMap()
    private val invokes = getInvokesGroupedByFiles(reflektInvokes.objects)

    override fun process(descriptor: DeclarationDescriptor): IrClassOrObjectUses =
        processClassOrObjectUses(descriptor, invokes, uses)

    override fun shouldRunOn(descriptor: DeclarationDescriptor): Boolean = descriptor.isPublicObject

}
