@file:OptIn(ObsoleteDescriptorBasedAPI::class)

package org.jetbrains.reflekt.plugin.analysis.processor.common

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction

// TODO: use IR properties instead of descriptors

internal val IrElement.isTopLevelPublicFunction: Boolean
    get() = this is IrFunction && this.descriptor.isTopLevelFunction && this.descriptor.isPublic

internal val IrElement.isPublicNotAbstractClass: Boolean
    get() = this is IrClass && this.descriptor.isPublicNotAbstractClass

internal val IrElement.isPublicObject: Boolean
    get() = this is IrClass && this.descriptor.isPublicObject
