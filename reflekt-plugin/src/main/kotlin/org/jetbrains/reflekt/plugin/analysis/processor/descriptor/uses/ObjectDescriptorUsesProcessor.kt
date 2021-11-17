package org.jetbrains.reflekt.plugin.analysis.processor.descriptor.uses

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.reflekt.plugin.analysis.models.ir.IrClassOrObjectUses
import org.jetbrains.reflekt.plugin.analysis.models.ReflektInvokes
import org.jetbrains.reflekt.plugin.analysis.processor.getInvokesGroupedByFiles
import org.jetbrains.reflekt.plugin.analysis.processor.isPublicObject

class ObjectDescriptorUsesProcessor(reflektInvokes: ReflektInvokes, override val messageCollector: MessageCollector?) :
    BaseDescriptorUsesProcessor<IrClassOrObjectUses>(messageCollector) {
    override val uses: IrClassOrObjectUses = HashMap()
    private val invokes = getInvokesGroupedByFiles(reflektInvokes.objects)

    override fun process(descriptor: DeclarationDescriptor): IrClassOrObjectUses =
        processClassOrObjectUses(descriptor, invokes, uses)

    override fun shouldRunOn(descriptor: DeclarationDescriptor): Boolean = descriptor.isPublicObject

}
