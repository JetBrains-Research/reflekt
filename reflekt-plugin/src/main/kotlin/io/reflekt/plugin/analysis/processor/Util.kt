package io.reflekt.plugin.analysis.processor

import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.isAbstract
import org.jetbrains.kotlin.psi.psiUtil.isPublic

internal val KtElement.isPublicObject: Boolean
    get() = this is KtObjectDeclaration && this.isPublic

internal val KtElement.isPublicFunction: Boolean
    get() = this is KtNamedFunction && this.isPublic

internal val KtElement.isPublicNotAbstractClass: Boolean
    get() = this is KtClass && this.isPublic && !this.isAbstract()

typealias FileID = String

// TODO: is it enough to identify a file?
internal val KtFile.fullName: FileID
    get() = "${this.packageFqName.asString()}.${this.name}"
