package org.jetbrains.reflekt.plugin.analysis.processor.common

import org.jetbrains.kotlin.descriptors.*

internal val DeclarationDescriptor.isPublicNotAbstractClass: Boolean
    get() = this is ClassDescriptor && this.isClass && !this.isAbstractClass && this.isPublic

internal val DeclarationDescriptor.isPublicObject: Boolean
    get() = this is ClassDescriptor && this.isObject && this.isPublic

internal val DeclarationDescriptor.isPublicTopLevelFunction: Boolean
    get() = this is FunctionDescriptor && this.isPublic && this.isTopLevelFunction

internal val DeclarationDescriptor.isMainFunction: Boolean
    get() = isPublicTopLevelFunction && (this as FunctionDescriptor).name.asString() == "main"

internal val FunctionDescriptor.isTopLevelFunction: Boolean
    get() = this.isTopLevelInPackage()

internal val FunctionDescriptor.isPublic: Boolean
    get() = this.visibility.isPublicAPI

internal val ClassDescriptor.isAbstractClass: Boolean
    get() = this.modality == Modality.ABSTRACT

internal val ClassDescriptor.isPublic: Boolean
    get() = this.visibility.isPublicAPI

internal val ClassDescriptor.isClass: Boolean
    get() = this.kind != ClassKind.OBJECT && this.kind != ClassKind.ENUM_ENTRY && this.kind != ClassKind.INTERFACE

internal val ClassDescriptor.isObject: Boolean
    get() = this.kind == ClassKind.OBJECT
