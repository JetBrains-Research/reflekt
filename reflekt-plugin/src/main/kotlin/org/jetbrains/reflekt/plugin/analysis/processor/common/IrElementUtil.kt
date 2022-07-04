package org.jetbrains.reflekt.plugin.analysis.processor.common

import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.util.isObject
import org.jetbrains.kotlin.ir.util.isClass
import org.jetbrains.kotlin.ir.util.isTopLevelDeclaration

internal val IrElement.isTopLevelPublicFunction: Boolean
    get() = this is IrFunction && this.isTopLevelDeclaration && this.visibility.isPublicAPI

internal val IrElement.isPublicNotAbstractClass: Boolean
    get() = this is IrClass && this.isClass && this.visibility.isPublicAPI && this.modality != Modality.ABSTRACT

internal val IrElement.isPublicObject: Boolean
    get() = this is IrClass && this.isObject && this.visibility.isPublicAPI
