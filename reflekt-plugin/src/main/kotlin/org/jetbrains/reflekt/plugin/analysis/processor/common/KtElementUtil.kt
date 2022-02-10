package org.jetbrains.reflekt.plugin.analysis.processor.common

import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.isAbstract
import org.jetbrains.kotlin.psi.psiUtil.isPublic

internal val KtElement.isPublicObject: Boolean
    get() = this is KtObjectDeclaration && this.isPublic

internal val KtElement.isPublicFunction: Boolean
    get() = this is KtNamedFunction && this.isPublic

internal val KtElement.isTopLevelPublicFunction: Boolean
    get() = isPublicFunction && (this as KtNamedFunction).isTopLevel

internal val KtElement.isMainFunction: Boolean
    get() = isPublicFunction && (this as KtNamedFunction).nameIdentifier?.let { it.text == "main" } ?: false

internal val KtElement.isPublicNotAbstractClass: Boolean
    get() = this is KtClass && this.isPublic && !this.isAbstract()
